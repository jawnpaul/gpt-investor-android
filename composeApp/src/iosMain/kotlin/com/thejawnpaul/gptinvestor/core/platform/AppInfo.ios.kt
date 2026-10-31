@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Singleton
import platform.Foundation.NSBundle

@Singleton
actual class AppInfo {
    actual val versionName: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: ""
}
