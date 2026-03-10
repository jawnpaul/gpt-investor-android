package com.thejawnpaul.gptinvestor.features.notification.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton
import timber.log.Timber

@Singleton
class TokenSyncManager(private val notificationRepository: NotificationRepository) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun syncToken() {
        scope.launch {
            try {
                notificationRepository.syncTokenIfNeeded()
            } catch (e: Exception) {
                Timber.e(e, "Error syncing token")
            }
        }
    }
}
