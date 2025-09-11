package com.thejawnpaul.gptinvestor.core.firebase

interface IRemoteConfig {

    suspend fun fetchAndActivateStringValue(configKey: String): String

    suspend fun fetchAndActivateValue(configKey: String): Float

    suspend fun fetchAndActivateBooleanValue(configKey: String): Boolean

}
