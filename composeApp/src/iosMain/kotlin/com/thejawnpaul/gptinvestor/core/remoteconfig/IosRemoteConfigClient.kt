package com.thejawnpaul.gptinvestor.core.remoteconfig

import org.koin.core.annotation.Singleton

@Singleton(binds = [RemoteConfigClient::class])
class IosRemoteConfigClient : RemoteConfigClient {
    override fun init() = Unit
    override fun fetchAndActivateStringValue(configKey: String): String? = null
    override fun fetchAndActivateDoubleValue(configKey: String): Double = 0.0
    override fun fetchAndActivateBooleanValue(configKey: String): Boolean = false
}
