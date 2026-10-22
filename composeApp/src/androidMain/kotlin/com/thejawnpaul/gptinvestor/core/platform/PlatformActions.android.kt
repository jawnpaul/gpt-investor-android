package com.thejawnpaul.gptinvestor.core.platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import org.koin.core.annotation.Factory

@Factory
actual class PlatformActions(private val context: Context, private val toastMessenger: ToastMessenger) {
    actual fun showMessage(message: String, icon: ImageVector?, actionText: String?, onAction: () -> Unit) {
        toastMessenger.show(message, icon, actionText, onAction)
    }

    actual fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clipData)
    }

    actual fun openUrl(url: String) {
        val parsedUri = runCatching { url.toUri() }.getOrNull() ?: return

        runCatching {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            customTabsIntent.launchUrl(context, parsedUri)
        }.onFailure {
            showMessage("No browser available to open this link")
        }
    }

    actual fun shareText(text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooser = Intent.createChooser(sendIntent, "Share via").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooser)
    }

    actual fun openNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:${context.packageName}".toUri())
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }
}
