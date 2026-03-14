package com.thejawnpaul.gptinvestor.core.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
    includes = [
        CommonAppModule::class,
        DatabaseModule::class
    ]
)
@ComponentScan("com.thejawnpaul.gptinvestor")
class IosAppModule
