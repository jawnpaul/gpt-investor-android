package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import org.koin.core.annotation.Singleton

@Singleton(binds = [NotificationPermissionController::class])
class IosNotificationPermissionController : NotificationPermissionController {
    @Composable
    override fun RequestPermissionIfNeeded(shouldRequest: Boolean, onGrant: () -> Unit, onDeny: () -> Unit) {
        if (shouldRequest) {
            onGrant()
        }
    }
}
