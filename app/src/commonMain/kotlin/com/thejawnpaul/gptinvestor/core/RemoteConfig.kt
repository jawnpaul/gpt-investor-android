package com.thejawnpaul.gptinvestor.core

interface RemoteConfig {

    fun init()

    fun fetchAndActivateStringValue(configKey: String): String

    fun fetchAndActivateValue(configKey: String): Float

    fun fetchAndActivateBooleanValue(configKey: String): Boolean

}