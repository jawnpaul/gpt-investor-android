package com.thejawnpaul.gptinvestor.features.authentication.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.platform.AppConfig
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.LoginRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.SignUpRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.User
import com.thejawnpaul.gptinvestor.features.guest.data.remote.GuestLoginRequest
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Singleton

@Singleton(binds = [AuthenticationRepository::class])
class AuthenticationRepositoryImpl(
    private val analyticsLogger: AnalyticsLogger,
    private val gptInvestorPreferences: AppPreferences,
    private val tokenSyncManager: TokenSyncManager,
    private val apiService: KtorApiService,
    private val tokenStorage: TokenStorage,
    private val appConfig: AppConfig
) : AuthenticationRepository {
    private val auth = Firebase.auth

    override val currentUser: User?
        get() = auth.currentUser?.let { firebaseUser ->
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                name = firebaseUser.displayName
            )
        }

    override suspend fun signOut() {
        try {
            auth.signOut()
            gptInvestorPreferences.clearUserId()
            gptInvestorPreferences.clearIsUserLoggedIn()
            gptInvestorPreferences.clearThemePreference()
            gptInvestorPreferences.clearIsUserOnModelWaitlist()
            gptInvestorPreferences.clearAccessToken()
            gptInvestorPreferences.clearRefreshToken()
            gptInvestorPreferences.clearIsGuestLoggedIn()
            analyticsLogger.resetUser(eventName = "Log Out")
            signOutPlatform()
        } catch (e: Exception) {
            Logger.e(e) { "Sign out failed" }
        }
    }

    override fun getAuthState(): Flow<Boolean> = flow {
        emit(tokenStorage.getAccessToken() != null)
    }

    override suspend fun deleteAccount() {
        try {
            auth.currentUser?.delete()
            analyticsLogger.resetUser(eventName = "Delete Account")
            gptInvestorPreferences.clearUserId()
            gptInvestorPreferences.clearIsUserLoggedIn()
            gptInvestorPreferences.clearThemePreference()
            gptInvestorPreferences.clearIsFirstInstall()
            gptInvestorPreferences.clearIsUserOnModelWaitlist()
            gptInvestorPreferences.clearAccessToken()
            gptInvestorPreferences.clearRefreshToken()
        } catch (e: Exception) {
            Logger.e(e) { "Delete account failed" }
        }
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
                tokenSyncManager.syncToken()
                tokenStorage.saveAccessToken(loginResponse.accessToken ?: "")
                tokenStorage.saveRefreshToken(loginResponse.refreshToken ?: "")
                gptInvestorPreferences.clearIsGuestLoggedIn()

                analyticsLogger.identifyUser(
                    eventName = "Log in",
                    params = mapOf(
                        "user_id" to loginResponse.user?.uid.toString(),
                        "email" to loginResponse.user?.email.toString()
                    )
                )
                Result.success(loginResponse.message ?: "Login successful")
            } ?: Result.failure(Exception(response.body?.message ?: "Login failed"))
        } else {
            val errorMessage = response.errorBody ?: "Login failed"
            Result.failure(Exception(errorMessage))
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

                    if (gptInvestorPreferences.isGuestLoggedIn.first() == true) {
                        analyticsLogger.logEvent(
                            eventName = "Guest Sign Up",
                            params = mapOf(
                                "user_id" to signUpResponse.userId.toString(),
                                "email" to email,
                                "sign_up_method" to "email_and_password",
                                "sign_up_source" to "guest_flow"
                            )
                        )
                    } else {
                        analyticsLogger.identifyUser(
                            eventName = "Sign Up",
                            params = mapOf(
                                "user_id" to signUpResponse.userId.toString(),
                                "email" to email,
                                "sign_up_method" to "email_and_password"
                            )
                        )
                    }

                    gptInvestorPreferences.clearIsGuestLoggedIn()
                    Result.success(signUpResponse.message ?: "Signup successful")
                } ?: Result.failure(Exception(response.body?.message ?: "Sign up failed"))
            } else {
                val errorMessage = response.errorBody ?: "Sign up failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun loginWithGoogle(platformContext: PlatformContext): Result<Unit> = loginWithGooglePlatform(
        auth = auth,
        apiService = apiService,
        gptInvestorPreferences = gptInvestorPreferences,
        tokenStorage = tokenStorage,
        tokenSyncManager = tokenSyncManager,
        platformContext = platformContext,
        appConfig = appConfig
    ).onSuccess {
        val isGuest = gptInvestorPreferences.isGuestLoggedIn.first() == true
        val params = buildMap {
            put("user_id", auth.currentUser?.uid ?: "")
            put("email", auth.currentUser?.email ?: "")
            put("log_in_method", "google")
            if (isGuest) {
                put("log_in_source", "guest_flow")
            }
        }
        analyticsLogger.identifyUser(
            eventName = "Log in",
            params = params
        )
    }

    override suspend fun signUpWithGoogle(platformContext: PlatformContext): Result<Unit> = loginWithGooglePlatform(
        auth = auth,
        apiService = apiService,
        gptInvestorPreferences = gptInvestorPreferences,
        tokenStorage = tokenStorage,
        tokenSyncManager = tokenSyncManager,
        platformContext = platformContext,
        appConfig = appConfig
    ).onSuccess {
        val isGuest = gptInvestorPreferences.isGuestLoggedIn.first() == true
        val params = buildMap {
            put("user_id", auth.currentUser?.uid ?: "")
            put("email", auth.currentUser?.email ?: "")
            put("sign_up_method", "google")
            if (isGuest) {
                put("sign_up_source", "guest_flow")
            }
        }
        analyticsLogger.identifyUser(
            eventName = "Sign Up",
            params = params
        )
    }

    override suspend fun guestLogin(): Result<String> = try {
        // val id = Firebase.installations.getId()
        val id = "mr paul"
        val response = apiService.guestLogin(request = GuestLoginRequest(id = id))
        if (response.isSuccessful) {
            response.body?.let { guestLoginResponse ->
                tokenStorage.saveAccessToken(guestLoginResponse.accessToken ?: "")
                gptInvestorPreferences.setIsGuestLoggedIn(true)
                analyticsLogger.logEvent(eventName = "Guest Session Start", params = mapOf())
                Result.success(guestLoginResponse.message ?: "Guest login successful")
            } ?: Result.failure(Exception(response.body?.message ?: "Guest login failed"))
        } else {
            val errorMessage = response.errorBody ?: "Guest login failed"
            return Result.failure(Exception(errorMessage))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

expect suspend fun signOutPlatform()

expect suspend fun loginWithGooglePlatform(
    auth: FirebaseAuth,
    apiService: KtorApiService,
    gptInvestorPreferences: AppPreferences,
    tokenStorage: TokenStorage,
    tokenSyncManager: TokenSyncManager,
    platformContext: PlatformContext,
    appConfig: AppConfig
): Result<Unit>
