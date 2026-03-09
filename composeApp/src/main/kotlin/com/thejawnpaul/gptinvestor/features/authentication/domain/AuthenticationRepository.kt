package com.thejawnpaul.gptinvestor.features.authentication.domain

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.FirebaseLoginRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.LoginRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.SignUpRequest
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Singleton
import timber.log.Timber

interface AuthenticationRepository {
    val currentUser: FirebaseUser?
    suspend fun signOut(activityContext: Context)
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount()
    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<String>
    suspend fun signUpWithEmailAndPassword(email: String, password: String, name: String): Result<String>

    suspend fun loginWithGoogle(activityContext: Context): Result<Unit>
}

@Singleton(binds = [AuthenticationRepository::class])
class AuthenticationRepositoryImpl(
    private val auth: FirebaseAuth,
    private val analyticsLogger: AnalyticsLogger,
    private val gptInvestorPreferences: GPTInvestorPreferences,
    private val tokenSyncManager: TokenSyncManager,
    private val apiService: KtorApiService,
    private val tokenStorage: TokenStorage
) : AuthenticationRepository {
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    private fun getCredentialManager(activityContext: Context): CredentialManager =
        CredentialManager.create(activityContext)

    override suspend fun signOut(activityContext: Context) {
        try {
            auth.signOut()
            gptInvestorPreferences.clearUserId()
            gptInvestorPreferences.clearIsUserLoggedIn()
            gptInvestorPreferences.clearThemePreference()
            gptInvestorPreferences.clearIsUserOnModelWaitlist()
            analyticsLogger.resetUser(eventName = "Log Out")
            val clearRequest = ClearCredentialStateRequest()
            val credentialManager = activityContext.getSystemService(CredentialManager::class.java)
            credentialManager.clearCredentialState(clearRequest)
        } catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
        }
    }

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Result<String> = try {
        val response = apiService.loginWithEmailAndPassword(
            request = LoginRequest(
                email,
                password
            )
        )
        if (response.isSuccessful) {
            response.body?.let { loginResponse ->
                gptInvestorPreferences.setUserId(loginResponse.user?.uid.toString())
                gptInvestorPreferences.setIsUserLoggedIn(true)
                gptInvestorPreferences.setUserName(loginResponse.user?.name.toString())
                tokenSyncManager.syncToken()
                tokenStorage.saveAccessToken(loginResponse.accessToken ?: "")
                tokenStorage.saveRefreshToken(loginResponse.refreshToken ?: "")

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
            response.errorBody?.let { error ->

                val errorMessage =
                    extractMessageFromErrorBody(error) ?: "An unknown error occurred."
                Result.failure(Exception(errorMessage))
            } ?: Result.failure(Exception(response.body?.message ?: "Login failed"))
        }
    } catch (e: Exception) {
        Timber.e(e.stackTraceToString())
        Result.failure(e)
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String, name: String): Result<String> =
        try {
            val response = apiService.signUpWithEmailAndPassword(
                request = SignUpRequest(
                    email = email,
                    password = password,
                    name = name
                )
            )
            if (response.isSuccessful) {
                response.body?.let { signUpResponse ->
                    analyticsLogger.identifyUser(
                        eventName = "Sign Up",
                        params = mapOf(
                            "user_id" to signUpResponse.userId.toString(),
                            "email" to email,
                            "sign_up_method" to "email_and_password"
                        )
                    )
                    Result.success(signUpResponse.message ?: "Sign up successful")
                } ?: Result.failure(Exception(response.body?.message ?: "Sign up failed"))
            } else {
                response.errorBody?.let { error ->

                    val errorMessage =
                        extractMessageFromErrorBody(error) ?: "An unknown error occurred."
                    Result.failure(Exception(errorMessage))
                } ?: Result.failure(Exception(response.body?.message ?: "Sign up failed"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }

    override suspend fun loginWithGoogle(activityContext: Context): Result<Unit> = try {
        val credentialManager = getCredentialManager(activityContext)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            request = request,
            context = activityContext
        )
        val credential = result.credential
        // Check if credential is of type Google ID
        if (credential is CustomCredential &&
            credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            auth.signInWithCredential(firebaseCredential).await()

            val firebaseIdToken = auth.currentUser?.getIdToken(false)?.await()?.token ?: ""

            val success = loginWithFirebase(firebaseIdToken)
            if (success) {
                tokenSyncManager.syncToken()
                gptInvestorPreferences.setUserName(auth.currentUser?.displayName.toString())
                analyticsLogger.identifyUser(
                    eventName = "Sign Up",
                    params = mapOf(
                        "user_id" to auth.currentUser?.uid.toString(),
                        "email" to auth.currentUser?.email.toString(),
                        "name" to auth.currentUser?.displayName.toString(),
                        "sign_up_method" to auth.currentUser?.providerId.toString()
                    )
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to login with backend"))
            }
        } else {
            Timber.e("Credential is not of type Google ID!")
            Result.failure(Exception("Credential is not of type Google ID!"))
        }
    } catch (e: Exception) {
        when (e) {
            is GetCredentialException -> {
                Timber.e("Get Credential Exception: ${e.stackTraceToString()}")
                Result.failure(e)
            }

            else -> {
                Timber.e("Unknown Exception: ${e.stackTraceToString()}")
                Result.failure(e)
            }
        }
    }

    private suspend fun loginWithFirebase(idToken: String): Boolean = try {
        val response = apiService.loginWithFirebase(
            request = FirebaseLoginRequest(idToken)
        )
        Timber.d(
            "Firebase login response: code=${response.code}, isSuccessful=${response.isSuccessful}"
        )
        if (response.isSuccessful) {
            response.body?.let { loginResponse ->
                Timber.d("Login response body received: $loginResponse")
                gptInvestorPreferences.setUserId(loginResponse.user?.uid.toString())
                gptInvestorPreferences.setIsUserLoggedIn(true)
                tokenStorage.saveAccessToken(loginResponse.accessToken ?: "")
                tokenStorage.saveRefreshToken(loginResponse.refreshToken ?: "")
                true
            } ?: run {
                Timber.e("Firebase login successful but body is null")
                false
            }
        } else {
            Timber.e(
                "Firebase login failed: code=${response.code}, error=${response.errorBody}"
            )
            false
        }
    } catch (e: Exception) {
        Timber.e(e, "Exception during Firebase login")
        false
    }

    private fun extractMessageFromErrorBody(errorBody: String): String? = try {
        // This is a simple manual parsing. For a more robust solution, use Moshi.
        // Example error JSON: {"message": "Invalid credentials"}
        val json = org.json.JSONObject(errorBody)
        json.getString("message")
    } catch (e: Exception) {
        Timber.e("Failed to parse error body: $errorBody")
        null
    }
}
