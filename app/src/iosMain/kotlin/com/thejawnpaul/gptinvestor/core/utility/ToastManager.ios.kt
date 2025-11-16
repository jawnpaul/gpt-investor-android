package com.thejawnpaul.gptinvestor.core.utility

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UILabel
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIViewAnimationOptionCurveEaseOut

actual open class ToastManager actual constructor() {
    @OptIn(ExperimentalForeignApi::class)
    actual fun showToast(
        message: String,
        duration: ToastDuration
    ) {
        val time = when (duration) {
            ToastDuration.Short -> 2.0
            ToastDuration.Long -> 5.0
        }

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        val uiScreenBounds = UIScreen.mainScreen.bounds

        val toastLabel = UILabel(
            frame = CGRectMake(
                0.0,
                0.0,
                uiScreenBounds.useContents { size.width } - 40,
                35.0
            )
        )

        toastLabel.apply {
            center = CGPointMake(
                uiScreenBounds.useContents { size.width } / 2,
                uiScreenBounds.useContents { size.height } - 100.0
            )
            textAlignment = NSTextAlignmentCenter
            backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.6)
            textColor = UIColor.whiteColor
            text = message
            alpha = 1.0
            layer.cornerRadius = 16.0
            clipsToBounds = true
        }
        rootViewController?.view?.addSubview(toastLabel)
        UIView.animateWithDuration(
            duration = time,
            delay = 0.1,
            options = UIViewAnimationOptionCurveEaseOut,
            animations = {
                toastLabel.alpha = 0.0
            },
            completion = {
                if (it) {
                    toastLabel.removeFromSuperview()
                }
            }
        )

    }
}