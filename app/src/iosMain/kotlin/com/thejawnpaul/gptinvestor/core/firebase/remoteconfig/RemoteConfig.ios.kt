package com.thejawnpaul.gptinvestor.core.firebase.remoteconfig

import co.touchlab.kermit.Logger
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfig
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigFetchAndActivateStatus
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigSettings
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
actual class RemoteConfig actual constructor() {
    private val remoteConfig = FIRRemoteConfig.remoteConfig().apply {
        val settings = FIRRemoteConfigSettings().apply {
            if (Platform.isDebugBinary) {
                minimumFetchInterval = 3600.0
            }
        }
        remoteConfig.setConfigSettings(settings)
    }
    actual fun init() {
        remoteConfig.setDefaultsFromPlistFileName("RemoteConfigDefaults")
        remoteConfig.addOnConfigUpdateListener { update, error ->
            if (update != null && error == null) {
                Logger.e("Updated keys: ${update.updatedKeys}")
            } else {
                Logger.e("Config update error with code: ${error?.code}")
            }
        }
    }

    actual fun fetchAndActivateStringValue(configKey: String): String {
        var result = remoteConfig.configValueForKey(configKey).stringValue()
        remoteConfig.fetchAndActivateWithCompletionHandler { status, _ ->
            when (status) {
                FIRRemoteConfigFetchAndActivateStatus
                    .FIRRemoteConfigFetchAndActivateStatusSuccessUsingPreFetchedData,
                FIRRemoteConfigFetchAndActivateStatus
                    .FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote ->
                    result = remoteConfig.configValueForKey(configKey).stringValue()

                else -> {}
            }
        }

        return result
    }

    actual fun fetchAndActivateValue(configKey: String): Float {
        var result = remoteConfig.configValueForKey(configKey).numberValue().floatValue()

        remoteConfig.fetchAndActivateWithCompletionHandler { status, _ ->
            when (status) {
                FIRRemoteConfigFetchAndActivateStatus
                    .FIRRemoteConfigFetchAndActivateStatusSuccessUsingPreFetchedData,
                FIRRemoteConfigFetchAndActivateStatus
                    .FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote ->
                    result = remoteConfig.configValueForKey(configKey).numberValue().floatValue()

                else -> {}
            }
        }

        return result
    }

    actual fun fetchAndActivateBooleanValue(configKey: String): Boolean {
        var result = remoteConfig.configValueForKey(configKey).boolValue()

        remoteConfig.fetchAndActivateWithCompletionHandler { status, _ ->
            when (status) {
                FIRRemoteConfigFetchAndActivateStatus
                    .FIRRemoteConfigFetchAndActivateStatusSuccessUsingPreFetchedData,
                FIRRemoteConfigFetchAndActivateStatus
                    .FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote ->
                    result = remoteConfig.configValueForKey(configKey).boolValue()

                else -> {}
            }
        }

        return result
    }
}