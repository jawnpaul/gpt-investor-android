package com.thejawnpaul.gptinvestor.core.remoteconfig

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.RemoteConfig
import timber.log.Timber
import javax.inject.Inject

class RemoteConfigImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : RemoteConfig {

    override fun init() {
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Timber.e("Updated keys: %s", configUpdate.updatedKeys)
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Timber.e("Config update error with code: %s", error.code)
            }
        })
    }

    override fun fetchAndActivateStringValue(configKey: String): String {
        var result = remoteConfig.getString(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getString(configKey)
            }
        }
        return result
    }

    override fun fetchAndActivateValue(configKey: String): Float {
        var result = remoteConfig.getDouble(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getDouble(configKey)
            }
        }
        return result.toFloat()
    }

    override fun fetchAndActivateBooleanValue(configKey: String): Boolean {
        var result = remoteConfig.getBoolean(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getBoolean(configKey)
            }
        }
        return result
    }
}
