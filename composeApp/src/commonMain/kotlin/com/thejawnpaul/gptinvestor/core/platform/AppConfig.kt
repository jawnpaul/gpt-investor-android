package com.thejawnpaul.gptinvestor.core.platform

import com.thejawnpaul.gptinvestor.shared.BuildConfig
import org.koin.core.annotation.Singleton

@Singleton
class AppConfig {
    val isDebug: Boolean = BuildConfig.DEBUG
    val webClientId: String = BuildConfig.WEB_CLIENT_ID
}
