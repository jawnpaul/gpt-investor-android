package com.thejawnpaul.gptinvestor.core.platform

import com.thejawnpaul.gptinvestor.shared.KmpBuildConfig
import org.koin.core.annotation.Singleton

@Singleton(binds = [AppConfig::class])
class AndroidAppConfig : AppConfig {
    override val isDebug: Boolean = KmpBuildConfig.DEBUG
    override val webClientId: String = KmpBuildConfig.WEB_CLIENT_ID
}
