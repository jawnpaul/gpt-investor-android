package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Factory
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

@Factory
actual class PlatformActions {
    actual fun showMessage(message: String) {
        val alert = UIAlertController.alertControllerWithTitle(
            title = null,
            message = message,
            preferredStyle = UIAlertControllerStyleAlert
        )
        alert.addAction(UIAlertAction.actionWithTitle("OK", UIAlertActionStyleDefault, null))
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(alert, animated = true, completion = null)
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
        rootViewController?.presentViewController(activityViewController, animated = true, completion = null)
    }
}
