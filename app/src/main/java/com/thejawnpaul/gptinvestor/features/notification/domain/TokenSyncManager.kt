package com.thejawnpaul.gptinvestor.features.notification.domain

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class TokenSyncManager @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
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
