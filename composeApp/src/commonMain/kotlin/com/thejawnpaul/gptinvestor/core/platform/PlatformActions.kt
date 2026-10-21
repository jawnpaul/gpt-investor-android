package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.core.annotation.Factory

@Factory
expect class PlatformActions {
    fun showMessage(message: String, icon: ImageVector? = null, actionText: String? = null, onAction: () -> Unit = {})
    fun copyToClipboard(label: String, text: String)
    fun openUrl(url: String)
    fun shareText(text: String)
}
