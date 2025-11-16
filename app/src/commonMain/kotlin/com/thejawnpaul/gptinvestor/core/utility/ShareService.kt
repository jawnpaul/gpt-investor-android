package com.thejawnpaul.gptinvestor.core.utility

expect open class ShareService() {
    fun showChooser(title: String, url: String, type: String = "text/plain")
}