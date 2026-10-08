package com.thejawnpaul.gptinvestor.features.authentication.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.platform.AppConfig
import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.utility.extractApiErrorMessage
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.FirebaseLoginRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.LoginRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.SignUpRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.User
import com.thejawnpaul.gptinvestor.features.guest.data.remote.GuestLoginRequest
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.remote.BearerTokenManager
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.installations.installations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

private const val EVENT_LOG_IN = "log-in"
private const val EVENT_SIGN_UP = "sign-up"
private const val EVENT_LOG_OUT = "log-out"
private const val EVENT_DELETE_ACCOUNT = "delete-account"
private const val EVENT_GUEST_SESSION_START = "guest-session-start"

private const val PARAM_USER_ID = "user_id"
private const val PARAM_EMAIL = "email"
private const val PARAM_LOG_IN_METHOD = "log_in_method"
private const val PARAM_SIGN_UP_METHOD = "sign_up_method"
private const val PARAM_LOG_IN_SOURCE = "log_in_source"
private const val PARAM_SIGN_UP_SOURCE = "sign_up_source"

private const val METHOD_GOOGLE = "google"
private const val METHOD_APPLE = "apple"
private const val METHOD_EMAIL = "email_and_password"

private const val SOURCE_GUEST = "guest_flow"
private const val SOURCE_DEFAULT = "default"

@Singleton(binds = [AuthenticationRepository::class])
class AuthenticationRepositoryImpl(
    @Provided private val analyticsLogger: AnalyticsLogger,
    private val gptInvestorPreferences: AppPreferences,
    private val tokenSyncManager: TokenSyncManager,
    private val apiService: KtorApiService,
    private val tokenStorage: TokenStorage,
    @Provided private val bearerTokenManager: BearerTokenManager,
    private val appConfig: AppConfig,
    @Provided private val googleSignInProvider: GoogleSignInProvider
) : AuthenticationRepository {
    private val auth by lazy { Firebase.auth }

    private val authDependencies: PlatformAuthDependencies
        get() = PlatformAuthDependencies(
            auth = auth,
            apiService = apiService,
            gptInvestorPreferences = gptInvestorPreferences,
            tokenStorage = tokenStorage,
            tokenSyncManager = tokenSyncManager,
            appConfig = appConfig
        )

    override val currentUser: User?
        get() = auth.currentUser?.let { firebaseUser ->
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                name = firebaseUser.displayName
            )
        }

    override suspend fun signOut(): Result<Unit> = try {
        auth.signOut()
        gptInvestorPreferences.clearUserId()
        gptInvestorPreferences.clearIsUserLoggedIn()
        gptInvestorPreferences.clearThemePreference()
        gptInvestorPreferences.clearIsUserOnModelWaitlist()
        gptInvestorPreferences.clearAccessToken()
        gptInvestorPreferences.clearRefreshToken()
        gptInvestorPreferences.clearIsGuestLoggedIn()
        analyticsLogger.resetUser(eventName = EVENT_LOG_OUT)
        signOutPlatform()
        Result.success(Unit)
    } catch (e: Exception) {
        Logger.e(e) { "Sign out failed" }
        Result.failure(e)
    }

    override fun getAuthState(): Flow<Boolean> = flow {
        emit(tokenStorage.getAccessToken() != null)
    }

    override suspend fun deleteAccount(): Result<Unit> = try {
        auth.currentUser?.delete()
        analyticsLogger.resetUser(eventName = EVENT_DELETE_ACCOUNT)
        gptInvestorPreferences.clearUserId()
        gptInvestorPreferences.clearIsUserLoggedIn()
        gptInvestorPreferences.clearThemePreference()
        gptInvestorPreferences.clearIsFirstInstall()
        gptInvestorPreferences.clearIsUserOnModelWaitlist()
        gptInvestorPreferences.clearAccessToken()
        gptInvestorPreferences.clearRefreshToken()
        Result.success(Unit)
    } catch (e: Exception) {
        Logger.e(e) { "Delete account failed" }
        Result.failure(e)
    }

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Result<String> = try {
        val response = apiService.loginWithEmailAndPassword(
            request = LoginRequest(email, password)
        )
        if (response.isSuccessful) {
            response.body?.let { loginResponse ->
                gptInvestorPreferences.setUserId(loginResponse.user?.uid.toString())
                gptInvestorPreferences.setIsUserLoggedIn(true)
                gptInvestorPreferences.setUserName(loginResponse.user?.name.toString())
                gptInvestorPreferences.clearIsGuestLoggedIn()
                tokenStorage.saveAccessToken(loginResponse.accessToken ?: "")
                tokenStorage.saveRefreshToken(loginResponse.refreshToken ?: "")
                bearerTokenManager.clearCache()
                tokenSyncManager.syncToken()

                trackAuthEvent(
                    isSignUp = false,
                    method = METHOD_EMAIL,
                    userId = loginResponse.user?.uid.toString(),
                    email = loginResponse.user?.email.toString()
                )
                Result.success(loginResponse.message ?: "Login successful")
            } ?: Result.failure(Exception(response.body?.message ?: "Login failed"))
        } else {
            if (response.code == 403) {
                Result.failure(EmailNotVerifiedException())
            } else {
                Result.failure(Exception(extractApiErrorMessage(response.errorBody, "Login failed")))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String, name: String): Result<String> =
        try {
            val response = apiService.signUpWithEmailAndPassword(
                SignUpRequest(email = email, password = password, name = name)
            )
            if (response.isSuccessful) {
                response.body?.let { signUpResponse ->
                    trackAuthEvent(
                        isSignUp = true,
                        method = METHOD_EMAIL,
                        userId = signUpResponse.userId.toString(),
                        email = email
                    )
                    gptInvestorPreferences.clearIsGuestLoggedIn()
                    Result.success(signUpResponse.message ?: "Signup successful")
                } ?: Result.failure(Exception(response.body?.message ?: "Sign up failed"))
            } else {
                if (response.code == 409) {
                    Result.failure(EmailAlreadyExistsException())
                } else {
                    Result.failure(Exception(extractApiErrorMessage(response.errorBody, "Sign up failed")))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun loginWithGoogle(platformContext: PlatformContext): Result<Unit> = loginWithGooglePlatform(
        dependencies = authDependencies,
        googleSignInProvider = googleSignInProvider,
        platformContext = platformContext
    ).onSuccess {
        bearerTokenManager.clearCache()
        trackAuthEvent(isSignUp = false, method = METHOD_GOOGLE)
    }

    override suspend fun loginWithApple(): Result<Unit> = loginWithApplePlatform(
        dependencies = authDependencies
    ).onSuccess {
        bearerTokenManager.clearCache()
        trackAuthEvent(isSignUp = false, method = METHOD_APPLE)
    }

    override suspend fun signUpWithGoogle(platformContext: PlatformContext): Result<Unit> = loginWithGooglePlatform(
        dependencies = authDependencies,
        googleSignInProvider = googleSignInProvider,
        platformContext = platformContext
    ).onSuccess {
        bearerTokenManager.clearCache()
        trackAuthEvent(isSignUp = true, method = METHOD_GOOGLE)
    }

    override suspend fun signUpWithApple(): Result<Unit> = loginWithApplePlatform(
        dependencies = authDependencies
    ).onSuccess {
        bearerTokenManager.clearCache()
        trackAuthEvent(isSignUp = true, method = METHOD_APPLE)
    }

    override suspend fun guestLogin(): Result<String> = try {
        val id = Firebase.installations.getId()
        val response = apiService.guestLogin(request = GuestLoginRequest(id = id))
        if (response.isSuccessful) {
            response.body?.let { guestLoginResponse ->
                tokenStorage.saveAccessToken(guestLoginResponse.accessToken ?: "")
                gptInvestorPreferences.setIsGuestLoggedIn(true)
                analyticsLogger.logEvent(eventName = EVENT_GUEST_SESSION_START, params = mapOf())
                Result.success(guestLoginResponse.message ?: "Guest login successful")
            } ?: Result.failure(Exception(response.body?.message ?: "Guest login failed"))
        } else {
            Result.failure(Exception(extractApiErrorMessage(response.errorBody, "Guest login failed")))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun acquireGuestToken(): Result<Unit> = try {
        val id = Firebase.installations.getId()
        val response = apiService.guestLogin(request = GuestLoginRequest(id = id))
        if (response.isSuccessful) {
            response.body?.let { loginResponse ->
                tokenStorage.saveAccessToken(loginResponse.accessToken ?: "")
                Result.success(Unit)
            } ?: Result.failure(Exception("Empty response"))
        } else {
            Result.failure(Exception(extractApiErrorMessage(response.errorBody, "Failed to acquire token")))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun trackAuthEvent(
        isSignUp: Boolean,
        method: String,
        userId: String? = null,
        email: String? = null
    ) {
        val isGuest = gptInvestorPreferences.isGuestLoggedIn.first() == true
        val eventName = if (isSignUp) EVENT_SIGN_UP else EVENT_LOG_IN
        val methodKey = if (isSignUp) PARAM_SIGN_UP_METHOD else PARAM_LOG_IN_METHOD
        val sourceKey = if (isSignUp) PARAM_SIGN_UP_SOURCE else PARAM_LOG_IN_SOURCE

        val params = buildMap {
            put(PARAM_USER_ID, userId ?: auth.currentUser?.uid ?: "")
            put(PARAM_EMAIL, email ?: auth.currentUser?.email ?: "")
            put(methodKey, method)
            if (isGuest) {
                put(sourceKey, SOURCE_GUEST)
            } else if (isSignUp) {
                put(sourceKey, SOURCE_DEFAULT)
            }
        }

        if (isSignUp && isGuest && method == METHOD_EMAIL) {
            analyticsLogger.logEvent(eventName, params)
        } else {
            analyticsLogger.identifyUser(eventName, params)
        }
    }
}

expect suspend fun signOutPlatform()

expect suspend fun loginWithGooglePlatform(
    dependencies: PlatformAuthDependencies,
    googleSignInProvider: GoogleSignInProvider,
    platformContext: PlatformContext
): Result<Unit>

expect suspend fun loginWithApplePlatform(dependencies: PlatformAuthDependencies): Result<Unit>

data class PlatformAuthDependencies(
    val auth: FirebaseAuth,
    val apiService: KtorApiService,
    val gptInvestorPreferences: AppPreferences,
    val tokenStorage: TokenStorage,
    val tokenSyncManager: TokenSyncManager,
    val appConfig: AppConfig
)

internal suspend fun completeLogin(
    dependencies: PlatformAuthDependencies,
    credential: AuthCredential,
    providerName: String,
    displayName: String? = null
) {
    val (auth, apiService, gptInvestorPreferences, tokenStorage, tokenSyncManager, _) = dependencies

    auth.signInWithCredential(authCredential = credential)

    val currentUser = auth.currentUser
        ?: throw Exception("No current user after $providerName sign-in")

    if (displayName != null) {
        currentUser.updateProfile(displayName = displayName)
    }

    val firebaseIdToken = currentUser.getIdToken(forceRefresh = true)
    val loginResponse = apiService.loginWithFirebase(
        request = FirebaseLoginRequest(idToken = firebaseIdToken ?: "")
    )
    if (!loginResponse.isSuccessful) throw Exception("Backend login failed: ${loginResponse.code}")

    val body = loginResponse.body ?: throw Exception("Empty response body")

    val nameToSet = body.user?.name ?: displayName ?: currentUser.displayName ?: "null"
    gptInvestorPreferences.setUserName(name = nameToSet)
    gptInvestorPreferences.setUserId(id = body.user?.uid.toString())
    gptInvestorPreferences.setIsUserLoggedIn(isLoggedIn = true)
    tokenStorage.saveAccessToken(token = body.accessToken ?: "")
    tokenStorage.saveRefreshToken(token = body.refreshToken ?: "")
    gptInvestorPreferences.clearIsGuestLoggedIn()
    tokenSyncManager.syncToken()
}
