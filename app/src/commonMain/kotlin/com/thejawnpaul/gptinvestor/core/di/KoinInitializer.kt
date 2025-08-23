package com.thejawnpaul.gptinvestor.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    config: KoinAppDeclaration? = null,
    platformModules: List<Module> = emptyList()
) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModules + providesDataStore()
                + provideDatabaseModule
                + providesCoroutinesModule
        )
    }
}