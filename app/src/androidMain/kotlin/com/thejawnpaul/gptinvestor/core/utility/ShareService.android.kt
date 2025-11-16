package com.thejawnpaul.gptinvestor.core.utility

import android.content.Intent

actual open class ShareService actual constructor() {
    actual fun showChooser(title: String, url: String, type: String) {
        val context = activityProvider.invoke() ?: return
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            this.type = type
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, url)
        }
        context.startActivity(
            Intent.createChooser(
                sendIntent,
                "Share via"
            )
        )
    }
}