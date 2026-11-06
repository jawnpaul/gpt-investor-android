@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.koin.core.annotation.Factory
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIImage
import platform.UIKit.UIPasteboard
import platform.UIKit.UIViewController

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
        topViewController()?.presentViewController(
            SFSafariViewController(uRL = nsUrl),
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

    actual suspend fun shareImage(png: ByteArray, text: String) {
        val image = UIImage.imageWithData(png.toNSData())
        if (image == null) {
            shareText(text)
            return
        }
        val activityViewController =
            UIActivityViewController(activityItems = listOf(image, text), applicationActivities = null)
        topViewController()?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }

    actual fun openNotificationSettings() {
        val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
    }

    private fun topViewController(): UIViewController? {
        var top = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (top?.presentedViewController != null) {
            top = top.presentedViewController
        }
        return top
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData = usePinned {
        NSData.create(bytes = it.addressOf(0), length = size.toULong())
    }
}
