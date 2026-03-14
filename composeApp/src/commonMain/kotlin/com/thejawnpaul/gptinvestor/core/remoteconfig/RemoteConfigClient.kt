package com.thejawnpaul.gptinvestor.core.remoteconfig

interface RemoteConfigClient {
    fun init()
    fun fetchAndActivateStringValue(configKey: String): String?
    fun fetchAndActivateDoubleValue(configKey: String): Double
    fun fetchAndActivateBooleanValue(configKey: String): Boolean
}
