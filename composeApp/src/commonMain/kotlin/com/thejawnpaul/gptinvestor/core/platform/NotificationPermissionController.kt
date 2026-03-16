package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable

interface NotificationPermissionController {
    @Composable
    fun RequestPermissionIfNeeded(shouldRequest: Boolean, onGrant: () -> Unit, onDeny: () -> Unit)
}
