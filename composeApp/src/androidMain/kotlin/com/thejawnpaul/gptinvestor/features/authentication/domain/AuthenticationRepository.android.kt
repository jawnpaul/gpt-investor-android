package com.thejawnpaul.gptinvestor.features.authentication.domain

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import co.touchlab.kermit.Logger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.platform.AndroidPlatformContext
import com.thejawnpaul.gptinvestor.core.platform.AppConfig
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.FirebaseLoginRequest
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider as GitLiveGoogleAuthProvider

actual suspend fun signOutPlatform() {
    // Credential Manager clear state is currently handled in the Android target
    // We can't easily get the context here without passing it or using a global provider
    // For now, we'll focus on the core Firebase sign out which is already handled
}

actual suspend fun loginWithGooglePlatform(
    auth: FirebaseAuth,
    apiService: KtorApiService,
    gptInvestorPreferences: AppPreferences,
    tokenStorage: TokenStorage,
    tokenSyncManager: TokenSyncManager,
    platformContext: PlatformContext,
    appConfig: AppConfig
): Result<Unit> = try {
    val androidContext = (platformContext as? AndroidPlatformContext)?.context
        ?: throw IllegalArgumentException("Invalid platform context")

    val credentialManager = CredentialManager.create(androidContext)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(appConfig.webClientId)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val result = credentialManager.getCredential(androidContext, request)
    val credential = result.credential

    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken = googleIdTokenCredential.idToken

        // Use GitLive to sign in
        auth.signInWithCredential(GitLiveGoogleAuthProvider.credential(idToken, null))

        val currentUser = auth.currentUser
        currentUser?.let {
            val firebaseIdToken = it.getIdToken(true)
            val loginResponse = apiService.loginWithFirebase(
                FirebaseLoginRequest(
                    firebaseIdToken ?: ""
                )
            )
            gptInvestorPreferences.setUserName(currentUser.displayName ?: "null")
            if (loginResponse.isSuccessful) {
                loginResponse.body?.let { response ->
                    gptInvestorPreferences.setUserId(response.user?.uid.toString())
                    gptInvestorPreferences.setIsUserLoggedIn(true)
                    response.user?.name?.let {
                        gptInvestorPreferences.setUserName(response.user.name)
                    }
                    tokenStorage.saveAccessToken(response.accessToken ?: "")
                    tokenStorage.saveRefreshToken(response.refreshToken ?: "")
                    tokenSyncManager.syncToken()
                }
            }
        }
        Result.success(Unit)
    } else {
        Result.failure(Exception("Credential is not of type Google ID!"))
    }
} catch (e: Exception) {
    Logger.e(e) { "Google login failed" }
    Result.failure(e)
}
