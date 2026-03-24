package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.platform.AppConfig
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import dev.gitlive.firebase.auth.FirebaseAuth

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
): Result<Unit> = Result.failure(IllegalStateException("Google Sign-In not supported on iOS yet"))
