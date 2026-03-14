package com.thejawnpaul.gptinvestor.core.platform

interface PlatformActions {
    fun showMessage(message: String)
    fun copyToClipboard(label: String, text: String)
    fun openUrl(url: String)
    fun shareText(text: String)
}
