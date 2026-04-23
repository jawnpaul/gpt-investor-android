package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.analytics.di.AnalyticsModule
import com.thejawnpaul.gptinvestor.remote.RemoteModule
import org.koin.core.annotation.Module

@Module(
    includes = [
        AnalyticsModule::class,
        CoroutinesModule::class,
        CoroutinesScopeModule::class,
        RemoteModule::class
    ]
)
class CommonAppModule
