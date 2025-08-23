package com.thejawnpaul.gptinvestor.core

interface IRemoteConfig {

    suspend fun fetchAndActivateStringValue(configKey: String): String

    suspend fun fetchAndActivateValue(configKey: String): Float

    suspend fun fetchAndActivateBooleanValue(configKey: String): Boolean

}