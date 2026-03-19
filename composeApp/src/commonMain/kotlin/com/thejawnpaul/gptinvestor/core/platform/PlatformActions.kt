package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Factory

@Factory
expect class PlatformActions {
    fun showMessage(message: String)
    fun copyToClipboard(label: String, text: String)
    fun openUrl(url: String)
    fun shareText(text: String)
}
