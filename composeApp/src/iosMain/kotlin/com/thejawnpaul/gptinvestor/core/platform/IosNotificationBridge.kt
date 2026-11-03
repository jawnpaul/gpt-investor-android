package com.thejawnpaul.gptinvestor.core.platform

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.features.notification.analytics.EVENT_NOTIFICATION_OPENED
import com.thejawnpaul.gptinvestor.features.notification.analytics.notificationOpenedParams
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Bridge between Swift and Kotlin for notification-related actions.
 */
object IosNotificationBridge : KoinComponent {
    private val notificationRepository: NotificationRepository by inject()

    // Held until App() navigates, so a tap that cold-starts the app isn't lost before the nav graph is ready.
    private val _pendingDeepLink = MutableStateFlow<String?>(null)
    val pendingDeepLink: StateFlow<String?> = _pendingDeepLink.asStateFlow()

    private var analyticsLogger: AnalyticsLogger? = null
    private val pendingOpens = mutableListOf<Map<String, Any>>()

    fun onNotificationOpened(deepLink: String?) {
        val params = notificationOpenedParams(deepLink)
        val logger = analyticsLogger
        if (logger != null) {
            logger.logEvent(EVENT_NOTIFICATION_OPENED, params)
        } else {
            pendingOpens.add(params)
        }
    }

    fun attachAnalytics(logger: AnalyticsLogger) {
        analyticsLogger = logger
        pendingOpens.forEach { logger.logEvent(EVENT_NOTIFICATION_OPENED, it) }
        pendingOpens.clear()
    }

    fun onNotificationTapped(deepLink: String) {
        _pendingDeepLink.value = deepLink
    }

    fun consumeDeepLink() {
        _pendingDeepLink.value = null
    }

    fun onFcmTokenReceived(token: String) {
        MainScope().launch {
            notificationRepository.saveToken(token)
            notificationRepository.syncTokenIfNeeded()
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

fun onNotificationTapped(deepLink: String) {
    IosNotificationBridge.onNotificationTapped(deepLink)
}

fun onNotificationOpened(deepLink: String?) {
    IosNotificationBridge.onNotificationOpened(deepLink)
}
