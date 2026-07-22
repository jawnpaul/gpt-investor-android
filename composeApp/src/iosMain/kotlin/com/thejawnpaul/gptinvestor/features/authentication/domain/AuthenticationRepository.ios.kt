package com.thejawnpaul.gptinvestor.features.authentication.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.bridges.AppleAuthProvider
import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
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
    val details: Pair<String, String> = suspendCancellableCoroutine { continuation ->
        googleSignInProvider.signIn(
            onSuccess = { token, accessToken -> continuation.resume(Pair(token, accessToken)) },
            onError = { continuation.resumeWithException(Exception(it)) }
        )
    }

    val credential = GitLiveGoogleAuthProvider.credential(
        idToken = details.first,
        accessToken = details.second
    )

    completeLogin(
        dependencies = dependencies,
        credential = credential,
        providerName = "Google"
    )
    Result.success(Unit)
} catch (e: Exception) {
    Logger.e(e) { "Google login failed on iOS" }
    Result.failure(e)
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun loginWithApplePlatform(dependencies: PlatformAuthDependencies): Result<Unit> = try {
    val appleAuthProvider = AppleAuthProvider.shared()

    val (credential, appleName) = suspendCancellableCoroutine { continuation ->
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

    completeLogin(
        dependencies = dependencies,
        credential = credential as AuthCredential,
        providerName = "Apple",
        displayName = appleName
    )
    Result.success(Unit)
} catch (e: Exception) {
    Logger.e("Error during Apple Sign-In: ${e.message}", e)
    Result.failure(e)
}
