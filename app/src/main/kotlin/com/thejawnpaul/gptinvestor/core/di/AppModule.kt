package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.analytics.di.AnalyticsModule
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfigModule
import com.thejawnpaul.gptinvestor.features.authentication.di.AuthModule
import com.thejawnpaul.gptinvestor.remote.RemoteModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [
    AnalyticsModule::class,
    AuthModule::class,
    CoroutinesModule::class,
    CoroutinesScopeModule::class,
    DatabaseModule::class,
    ImageModule::class,
    RemoteModule::class,
    RemoteConfigModule::class,
])
@ComponentScan("com.thejawnpaul.gptinvestor")
class AppModule