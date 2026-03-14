package com.thejawnpaul.gptinvestor.core.di

import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [IosAppModule::class])
class IosKoinApplication

fun initKoin() {
    startKoin<IosKoinApplication>()
}
