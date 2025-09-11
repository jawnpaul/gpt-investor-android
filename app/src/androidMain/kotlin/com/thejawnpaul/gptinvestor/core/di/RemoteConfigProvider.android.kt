package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.firebase.IRemoteConfig
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfigImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun providesRemoteConfig(config: IRemoteConfig?): Module = module {
    single<IRemoteConfig> { RemoteConfigImpl() }
}
