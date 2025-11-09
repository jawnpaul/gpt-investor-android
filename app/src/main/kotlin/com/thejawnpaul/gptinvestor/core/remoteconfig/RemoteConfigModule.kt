package com.thejawnpaul.gptinvestor.core.remoteconfig

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.thejawnpaul.gptinvestor.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteConfigModule {

    @Provides
    @Singleton
    fun providesRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = if (BuildConfig.DEBUG) {
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
        } else {
            remoteConfigSettings { }
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        return remoteConfig
    }
}
