package com.thejawnpaul.gptinvestor.core.firebase.remoteconfig

expect class RemoteConfig() {

    fun init()

    fun fetchAndActivateStringValue(configKey: String): String

    fun fetchAndActivateValue(configKey: String): Float

    fun fetchAndActivateBooleanValue(configKey: String): Boolean
}
