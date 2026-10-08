package com.thejawnpaul.gptinvestor.core.platform

import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Bridge between Swift and Kotlin for notification-related actions.
 */
object IosNotificationBridge : KoinComponent {
    private val notificationRepository: NotificationRepository by inject()

    fun onFcmTokenReceived(token: String) {
        MainScope().launch {
            notificationRepository.saveToken(token)
        }
    }
}

/**
 * Top-level function to be called from Swift as IosNotificationBridgeKt.onFcmTokenReceived(token:).
 * Kotlin top-level functions in a file named `Filename.kt` are exposed to Swift via `FilenameKt` class.
 */
fun onFcmTokenReceived(token: String) {
    IosNotificationBridge.onFcmTokenReceived(token)
}
