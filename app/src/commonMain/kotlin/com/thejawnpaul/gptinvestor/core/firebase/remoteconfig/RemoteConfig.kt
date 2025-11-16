package com.thejawnpaul.gptinvestor.core.firebase.remoteconfig

import co.touchlab.kermit.Logger
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.thejawnpaul.gptinvestor.R

class RemoteConfig(private val remoteConfig: FirebaseRemoteConfig) {

    fun init() {
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

    fun fetchAndActivateStringValue(configKey: String): String {
        var result = remoteConfig.getString(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getString(configKey)
            }
        }
        return result
    }

    fun fetchAndActivateValue(configKey: String): Float {
        var result = remoteConfig.getDouble(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getDouble(configKey)
            }
        }
        return result.toFloat()
    }

    fun fetchAndActivateBooleanValue(configKey: String): Boolean {
        var result = remoteConfig.getBoolean(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getBoolean(configKey)
            }
        }
        return result
    }
}
