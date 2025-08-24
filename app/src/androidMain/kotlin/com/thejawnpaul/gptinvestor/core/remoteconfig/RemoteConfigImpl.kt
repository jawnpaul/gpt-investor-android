package com.thejawnpaul.gptinvestor.core.remoteconfig

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.firebase.IRemoteConfig
import org.koin.core.annotation.Single
import timber.log.Timber
import com.google.firebase.remoteconfig.FirebaseRemoteConfig as RemoteConfig

@Single
class RemoteConfigImpl : IRemoteConfig {

    var remoteConfig: RemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = if (BuildConfig.DEBUG) {
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
        } else {
            remoteConfigSettings { }
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
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

    override suspend fun fetchAndActivateStringValue(configKey: String): String {
        var result = remoteConfig.getString(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getString(configKey)
            }
        }
        return result
    }

    override suspend fun fetchAndActivateValue(configKey: String): Float {
        var result = remoteConfig.getDouble(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getDouble(configKey)
            }
        }
        return result.toFloat()
    }

    override suspend fun fetchAndActivateBooleanValue(configKey: String): Boolean {
        var result = remoteConfig.getBoolean(configKey)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = remoteConfig.getBoolean(configKey)
            }
        }
        return result
    }
}
