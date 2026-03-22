package com.thejawnpaul.gptinvestor.features.authentication.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.platform.AppConfig
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.FirebaseLoginRequest
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider as GitLiveGoogleAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.mp.KoinPlatform.getKoin

actual suspend fun signOutPlatform() {
    // No-op for parity v1
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
    val googleSignInProvider: GoogleSignInProvider = getKoin().get()

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
