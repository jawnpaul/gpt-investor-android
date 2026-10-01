package com.thejawnpaul.gptinvestor.features.notification.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenRequest
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Singleton

@Singleton(binds = [NotificationRepository::class])
class NotificationRepositoryImpl(private val apiService: KtorApiService, private val preferences: AppPreferences) :
    NotificationRepository {

    override suspend fun saveToken(token: String) {
        preferences.setFcmToken(token)
        preferences.setIsTokenSynced(false)
        Logger.e { "FCM token saved to preferences." }
    }

    override suspend fun syncTokenIfNeeded() {
        val isGuest = preferences.isGuestLoggedIn.first() == true
        if (isGuest) {
            Logger.e { "Token sync skipped: guest session." }
            return
        }

        val userId = preferences.userId.first()
        val token = preferences.fcmToken.first()
        val isTokenSynced = preferences.isTokenSynced.first()

        if (userId != null && token != null && !isTokenSynced) {
            Logger.e { "Attempting to sync token for user: $userId" }
            registerToken(token)
        } else {
            Logger.e { "Token sync skipped. UserID: $userId, Token present: ${token != null}, Synced: $isTokenSynced" }
        }
    }

    private suspend fun registerToken(token: String) {
        try {
            val response = apiService.registerToken(RegisterTokenRequest(token = token))
            if (response.isSuccessful) {
                preferences.setIsTokenSynced(true)
                Logger.e { "FCM token successfully registered" }
            } else {
                Logger.e { "Failed to register FCM token: HTTP ${response.code}" }
            }
        } catch (e: Exception) {
            Logger.e(e) { "Failed to register FCM token" }
        }
    }
}
