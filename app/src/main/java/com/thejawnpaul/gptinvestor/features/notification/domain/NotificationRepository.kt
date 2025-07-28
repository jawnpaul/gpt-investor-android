package com.thejawnpaul.gptinvestor.features.notification.domain

import com.google.firebase.messaging.FirebaseMessaging
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenRequest
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

interface NotificationRepository {
    suspend fun saveToken(token: String)
    suspend fun syncTokenIfNeeded()
}

class NotificationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferences: GPTInvestorPreferences
) : NotificationRepository {

    override suspend fun saveToken(token: String) {
        preferences.setFcmToken(token)
        preferences.setIsTokenSynced(false)
        Timber.e("FCM token saved to preferences.")
    }

    override suspend fun syncTokenIfNeeded() {
        val userId = preferences.userId.first()
        val token = preferences.fcmToken.first()
        val isTokenSynced = preferences.isTokenSynced.first()

        if (userId != null && token != null && !isTokenSynced) {
            Timber.e("Attempting to sync token for user: $userId")
            registerToken(token, userId)
        } else {
            if (token == null) {
                Timber.e("FCM token is null")
                FirebaseMessaging.getInstance().token.addOnSuccessListener { newToken ->
                    CoroutineScope(Dispatchers.IO).launch {
                        userId?.let {
                            preferences.setFcmToken(newToken)
                            registerToken(newToken, userId)
                        }
                    }
                }
            } else {
                Timber.e("Token sync not needed. UserID: $userId, Synced: $isTokenSynced")
            }
        }
    }

    private suspend fun registerToken(token: String, userId: String) {
        try {
            apiService.registerToken(RegisterTokenRequest(token = token, userId = userId))
            preferences.setIsTokenSynced(true)
            Timber.e("FCM token successfully registered for user: $userId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to register FCM token for user: $userId")
            // The token remains unsynced, will be retried on next trigger.
        }
    }
}
