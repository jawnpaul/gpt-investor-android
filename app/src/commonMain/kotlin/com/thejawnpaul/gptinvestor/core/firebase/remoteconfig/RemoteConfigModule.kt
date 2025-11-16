package com.thejawnpaul.gptinvestor.core.firebase.remoteconfig

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.thejawnpaul.gptinvestor.BuildConfig
import org.koin.dsl.module

val remoteConfigModule = module {

    single<FirebaseRemoteConfig> {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    if (BuildConfig.DEBUG) {
                        minimumFetchIntervalInSeconds = 3600
                    }
                }
            )
        }
    }

    single<RemoteConfig> {
        RemoteConfig(get())
    }
}
