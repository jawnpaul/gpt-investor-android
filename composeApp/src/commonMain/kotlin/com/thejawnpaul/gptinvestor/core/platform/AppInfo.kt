package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Singleton

@Singleton
expect class AppInfo {
    val versionName: String
}
