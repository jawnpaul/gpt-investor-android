package com.thejawnpaul.gptinvestor.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val coroutinesScopeModule = module {
    single<CoroutineScope> {
        val defaultDispatcher: CoroutineDispatcher = get(DefaultDispatcher)
        CoroutineScope(SupervisorJob() + defaultDispatcher)
    }
}
