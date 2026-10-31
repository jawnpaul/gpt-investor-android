package com.thejawnpaul.gptinvestor.core.platform

import android.content.Context
import org.koin.core.annotation.Singleton

@Singleton
actual class AppInfo(context: Context) {
    actual val versionName: String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
}
