package com.thejawnpaul.gptinvestor.core.firebase.remoteconfig

import co.touchlab.kermit.Logger
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.R

actual class RemoteConfig actual constructor() {
    private val remoteConfig = Firebase.remoteConfig.apply {
        setConfigSettingsAsync(
            remoteConfigSettings {
                if (BuildConfig.DEBUG) {
                    minimumFetchIntervalInSeconds = 3600
                }
            }
        )
    }
    actual fun init() {
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Logger.e("Updated keys: ${configUpdate.updatedKeys}")
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Logger.e("Config update error with code: ${error.code}")
            }
        })
    }

    actual fun fetchAndActivateStringValue(configKey: String): String {
        var result = remoteConfig.getString(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getString(configKey)
            }
        }
        return result
    }

    actual fun fetchAndActivateValue(configKey: String): Float {
        var result = remoteConfig.getDouble(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getDouble(configKey)
            }
        }
        return result.toFloat()
    }

    actual fun fetchAndActivateBooleanValue(configKey: String): Boolean {
        var result = remoteConfig.getBoolean(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getBoolean(configKey)
            }
        }
        return result
    }
}