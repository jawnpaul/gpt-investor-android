package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfigModule
import com.thejawnpaul.gptinvestor.features.authentication.di.AuthModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
    includes = [
        CommonAppModule::class,
        AuthModule::class,
        DatabaseModule::class,
        PreferenceModule::class,
        ImageModule::class,
        RemoteConfigModule::class
    ]
)
@ComponentScan("com.thejawnpaul.gptinvestor")
class AndroidAppModule
