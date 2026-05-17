@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.thejawnpaul.gptinvestor.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.NSLineBreakByWordWrapping
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UILabel
import platform.UIKit.UIPasteboard
import platform.UIKit.UIView
import platform.UIKit.UIViewAnimationOptionCurveEaseOut
import platform.UIKit.UIViewContentMode

@Factory
actual class PlatformActions {
    @OptIn(ExperimentalForeignApi::class)
    actual fun showMessage(message: String) {
        MainScope().launch {
            val window = UIApplication.sharedApplication.keyWindow ?: return@launch

            val horizontalPadding = 16.0
            val verticalPadding = 16.0
            val iconSize = 24.0
            val iconTextGap = 12.0
            val textFont = UIFont.boldSystemFontOfSize(14.0)

            val windowWidth = window.frame.useContents { size.width }
            val windowHeight = window.frame.useContents { size.height }

            val maxContainerWidth = windowWidth - 32.0
            val maxTextWidth = maxContainerWidth - horizontalPadding - iconSize - iconTextGap - horizontalPadding

            val bundle = NSBundle.mainBundle
            val iconsDict = bundle.infoDictionary?.get("CFBundleIcons") as? Map<*, *>
            val primaryIconDict = iconsDict?.get("CFBundlePrimaryIcon") as? Map<*, *>
            val iconFiles = primaryIconDict?.get("CFBundleIconFiles") as? List<*>
            val iconName = iconFiles?.lastOrNull() as? String
            val appIconImage =
                if (iconName != null) UIImage.imageNamed(iconName) else UIImage.imageNamed("AppIcon")

            val icon = UIImageView().apply {
                image = appIconImage
                contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                clipsToBounds = true
                layer.cornerRadius = iconSize / 2
                setFrame(CGRectMake(0.0, 0.0, iconSize, iconSize))
            }

            val messageLabel = UILabel().apply {
                text = message
                font = textFont
                textColor = UIColor.blackColor.colorWithAlphaComponent(0.8)
                numberOfLines = 2L
                lineBreakMode = NSLineBreakByWordWrapping
            }

            messageLabel.setFrame(CGRectMake(0.0, 0.0, maxTextWidth, 0.0))
            messageLabel.sizeToFit()

            val messageWidth = messageLabel.frame.useContents { size.width }
            val messageHeight = messageLabel.frame.useContents { size.height }

            val containerWidth = horizontalPadding + iconSize + iconTextGap + messageWidth + horizontalPadding
            val containerHeight = maxOf(messageHeight, iconSize) + (verticalPadding * 2)

            val containerX = (windowWidth - containerWidth) / 2
            val containerY = windowHeight - 120.0 - containerHeight

            val container = UIView().apply {
                backgroundColor = UIColor.whiteColor.colorWithAlphaComponent(0.95)
                layer.cornerRadius = containerHeight / 2
                clipsToBounds = true
            }
            container.setFrame(CGRectMake(containerX, containerY, containerWidth, containerHeight))

            val iconY = (containerHeight - iconSize) / 2
            icon.setFrame(CGRectMake(horizontalPadding, iconY, iconSize, iconSize))

            val textX = horizontalPadding + iconSize + iconTextGap
            val messageLabelY = (containerHeight - messageHeight) / 2
            messageLabel.setFrame(CGRectMake(textX, messageLabelY, messageWidth, messageHeight))

            container.addSubview(icon)
            container.addSubview(messageLabel)
            window.addSubview(container)

            UIView.animateWithDuration(
                duration = 0.3,
                delay = 2.0,
                options = UIViewAnimationOptionCurveEaseOut,
                animations = { container.alpha = 0.0 },
                completion = { _ -> container.removeFromSuperview() }
            )
        }
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
}
