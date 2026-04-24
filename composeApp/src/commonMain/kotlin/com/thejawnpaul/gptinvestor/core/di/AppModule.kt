package com.thejawnpaul.gptinvestor.core.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
    includes = [
        CommonAppModule::class,
        AuthModule::class,
        DatabaseModule::class,
        PreferenceModule::class,
        RemoteConfigModule::class
    ]
)
@ComponentScan("com.thejawnpaul.gptinvestor.features", "com.thejawnpaul.gptinvestor.core")
class AppModule
