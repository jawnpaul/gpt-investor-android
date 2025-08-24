package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.firebase.IRemoteConfig
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun providesRemoteConfig(config: IRemoteConfig?): Module = module {
    config?.let { remoteConfig ->
        single<IRemoteConfig> { remoteConfig }
    }
}