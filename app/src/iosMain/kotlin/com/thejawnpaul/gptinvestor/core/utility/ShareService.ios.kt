package com.thejawnpaul.gptinvestor.core.utility

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual open class ShareService actual constructor() {
    @OptIn(BetaInteropApi::class)
    actual fun showChooser(title: String, url: String, type: String) {
        val activityTerms = listOf(
            NSString.create(string = title),
            NSString.create(string = url)
        )
        val activityController =
            UIActivityViewController(activityItems = activityTerms, applicationActivities = null)
        UIApplication.sharedApplication.keyWindow
            ?.rootViewController
            ?.presentViewController(activityController, animated = true, completion = null)

    }
}