package com.thejawnpaul.gptinvestor.features.authentication.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.bridges.AppleAuthProvider
import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.FirebaseLoginRequest
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.OAuthProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import dev.gitlive.firebase.auth.GoogleAuthProvider as GitLiveGoogleAuthProvider

actual suspend fun signOutPlatform() {
    // No-op for parity v1
}

actual suspend fun loginWithGooglePlatform(
    dependencies: PlatformAuthDependencies,
    googleSignInProvider: GoogleSignInProvider,
    platformContext: PlatformContext
): Result<Unit> = try {
    val (auth, apiService, gptInvestorPreferences, tokenStorage, tokenSyncManager, appConfig) = dependencies

    val details: Pair<String, String> = suspendCancellableCoroutine { continuation ->
        googleSignInProvider.signIn(
            onSuccess = { token, accessToken -> continuation.resume(Pair(token, accessToken)) },
            onError = { continuation.resumeWithException(Exception(it)) }
        )
    }

    auth.signInWithCredential(
        GitLiveGoogleAuthProvider.credential(
            details.first,
            details.second
        )
    )

    val currentUser = auth.currentUser
    currentUser?.let {
        val firebaseIdToken = it.getIdToken(true)
        val loginResponse = apiService.loginWithFirebase(
            FirebaseLoginRequest(firebaseIdToken ?: "")
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
} catch (e: Exception) {
    Logger.e(e) { "Google login failed on iOS" }
    Result.failure(e)
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun loginWithApplePlatform(dependencies: PlatformAuthDependencies): Result<Unit> = try {
    val (auth, apiService, gptInvestorPreferences, tokenStorage, tokenSyncManager, appConfig) = dependencies
    val appleAuthProvider = AppleAuthProvider.shared()

    val details: AuthCredential = suspendCancellableCoroutine { continuation ->
        appleAuthProvider.signInWithApple(
            onSuccess = { token, nonce ->
                if (token != null && nonce != null) {
                    OAuthProvider.credential(
                        providerId = "apple.com",
                        idToken = token,
                        rawNonce = nonce,
                        accessToken = null
                    ).let { credential ->
                        continuation.resume(credential)
                    }
                } else {
                    continuation.resumeWithException(Exception("Unknown Apple Sign In failure"))
                }
            },
            onError = { continuation.resumeWithException(Exception(it)) }
        )
    }

    auth.signInWithCredential(details)
    val currentUser = auth.currentUser
    currentUser?.let {
        val firebaseIdToken = it.getIdToken(true)
        val loginResponse = apiService.loginWithFirebase(
            FirebaseLoginRequest(firebaseIdToken ?: "")
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
} catch (e: Exception) {
    Logger.e("Error during Apple Sign-In: ${e.message}", e)
    Result.failure(e)
}
