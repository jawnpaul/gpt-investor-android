package com.thejawnpaul.gptinvestor.features.authentication.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.bridges.AppleAuthProvider
import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.FirebaseLoginRequest
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.GoogleAuthProvider as GitLiveGoogleAuthProvider
import dev.gitlive.firebase.auth.OAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine

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
        ?: return@try Result.failure(Exception("No current user after Google sign-in"))
    val firebaseIdToken = currentUser.getIdToken(true)
    val loginResponse = apiService.loginWithFirebase(FirebaseLoginRequest(firebaseIdToken ?: ""))
    if (!loginResponse.isSuccessful) return@try Result.failure(Exception("Backend login failed: ${loginResponse.code}"))
    val body = loginResponse.body ?: return@try Result.failure(Exception("Empty response body"))

    gptInvestorPreferences.setUserName(body.user?.name ?: currentUser.displayName ?: "null")
    gptInvestorPreferences.setUserId(body.user?.uid.toString())
    gptInvestorPreferences.setIsUserLoggedIn(true)
    tokenStorage.saveAccessToken(body.accessToken ?: "")
    tokenStorage.saveRefreshToken(body.refreshToken ?: "")
    gptInvestorPreferences.clearIsGuestLoggedIn()
    tokenSyncManager.syncToken()
    Result.success(Unit)
} catch (e: Exception) {
    Logger.e(e) { "Google login failed on iOS" }
    Result.failure(e)
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun loginWithApplePlatform(dependencies: PlatformAuthDependencies): Result<Unit> = try {
    val (auth, apiService, gptInvestorPreferences, tokenStorage, tokenSyncManager, appConfig) = dependencies
    val appleAuthProvider = AppleAuthProvider.shared()

    val (details, appleName) = suspendCancellableCoroutine { continuation ->
        appleAuthProvider.signInWithApple(
            onSuccess = { token: String?, nonce: String?, givenName: String?, familyName: String? ->
                if (token != null && nonce != null) {
                    val name = listOfNotNull(givenName, familyName).joinToString(" ").ifBlank { null }
                    val credential = OAuthProvider.credential(
                        providerId = "apple.com",
                        idToken = token,
                        rawNonce = nonce,
                        accessToken = null
                    )
                    continuation.resume(Pair(credential, name))
                } else {
                    continuation.resumeWithException(Exception("Unknown Apple Sign In failure"))
                }
            },
            onError = { continuation.resumeWithException(Exception(it)) }
        )
    }

    auth.signInWithCredential(details as AuthCredential)
    val currentUser = auth.currentUser
        ?: return@try Result.failure(Exception("No current user after Apple sign-in"))
    if (appleName != null) {
        currentUser.updateProfile(displayName = appleName)
    }
    val firebaseIdToken = currentUser.getIdToken(true)
    val loginResponse = apiService.loginWithFirebase(FirebaseLoginRequest(firebaseIdToken ?: ""))
    if (!loginResponse.isSuccessful) return@try Result.failure(Exception("Backend login failed: ${loginResponse.code}"))
    val body = loginResponse.body ?: return@try Result.failure(Exception("Empty response body"))

    val nameToSet = body.user?.name ?: appleName ?: currentUser.displayName ?: "null"
    gptInvestorPreferences.setUserName(nameToSet)
    gptInvestorPreferences.setUserId(body.user?.uid.toString())
    gptInvestorPreferences.setIsUserLoggedIn(true)
    tokenStorage.saveAccessToken(body.accessToken ?: "")
    tokenStorage.saveRefreshToken(body.refreshToken ?: "")
    gptInvestorPreferences.clearIsGuestLoggedIn()
    tokenSyncManager.syncToken()
    Result.success(Unit)
} catch (e: Exception) {
    Logger.e("Error during Apple Sign-In: ${e.message}", e)
    Result.failure(e)
}
