@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.core.annotation.Factory
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIPasteboard

@Factory
actual class PlatformActions(private val toastMessenger: ToastMessenger) {
    actual fun showMessage(message: String, icon: ImageVector?, actionText: String?, onAction: () -> Unit) {
        toastMessenger.show(message, icon, actionText, onAction)
    }

    actual fun copyToClipboard(label: String, text: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        val safariViewController = SFSafariViewController(uRL = nsUrl)
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        var topViewController = rootViewController
        while (topViewController?.presentedViewController != null) {
            topViewController = topViewController.presentedViewController
        }
        topViewController?.presentViewController(
            safariViewController,
            animated = true,
            completion = null
        )
    }

    actual fun shareText(text: String) {
        val activityViewController =
            UIActivityViewController(activityItems = listOf(text), applicationActivities = null)
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }

    actual fun openNotificationSettings() {
        val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
    }
}
