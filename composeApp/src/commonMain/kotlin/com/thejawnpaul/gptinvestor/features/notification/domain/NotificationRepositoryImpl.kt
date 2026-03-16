package com.thejawnpaul.gptinvestor.features.notification.domain

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenRequest
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
        val userId = preferences.userId.first()
        val token = preferences.fcmToken.first()
        val isTokenSynced = preferences.isTokenSynced.first()

        if (userId != null && token != null && !isTokenSynced) {
            Logger.e { "Attempting to sync token for user: $userId" }
            registerToken(token)
        } else {
            if (token == null) {
                Logger.e { "FCM token is null" }
                try {
                    val newToken = Firebase.messaging.getToken()
                    CoroutineScope(Dispatchers.IO).launch {
                        userId?.let {
                            preferences.setFcmToken(newToken)
                            registerToken(newToken)
                        }
                    }
                } catch (e: Exception) {
                    Logger.e(e) { "Failed to get FCM token" }
                }
            } else {
                Logger.e { "Token sync not needed. UserID: $userId, Synced: $isTokenSynced" }
            }
        }
    }

    private suspend fun registerToken(token: String) {
        try {
            apiService.registerToken(RegisterTokenRequest(token = token))
            preferences.setIsTokenSynced(true)
            Logger.e { "FCM token successfully registered" }
        } catch (e: Exception) {
            Logger.e(e) { "Failed to register FCM token" }
        }
    }
}
