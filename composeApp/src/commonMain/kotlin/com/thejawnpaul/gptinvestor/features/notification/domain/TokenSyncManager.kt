package com.thejawnpaul.gptinvestor.features.notification.domain

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton

@Singleton
class TokenSyncManager(private val notificationRepository: NotificationRepository) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun syncToken() {
        scope.launch {
            try {
                notificationRepository.syncTokenIfNeeded()
            } catch (e: Exception) {
                Logger.e(messageString = "Error syncing token", throwable = e.cause)
            }
        }
    }
}
