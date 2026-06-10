package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

@Singleton(binds = [NotificationPermissionController::class])
class IosNotificationPermissionController : NotificationPermissionController {
    @Composable
    override fun RequestPermissionIfNeeded(shouldRequest: Boolean, onGrant: () -> Unit, onDeny: () -> Unit) {
        if (!shouldRequest) return

        LaunchedEffect(Unit) {
            val granted = suspendCancellableCoroutine<Boolean> { continuation ->
                UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
                    options = UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound,
                    completionHandler = { granted, _ -> continuation.resume(granted) }
                )
            }
            withContext(Dispatchers.Main) {
                if (granted) {
                    UIApplication.sharedApplication().registerForRemoteNotifications()
                    onGrant()
                } else {
                    onDeny()
                }
            }
        }
    }
}
