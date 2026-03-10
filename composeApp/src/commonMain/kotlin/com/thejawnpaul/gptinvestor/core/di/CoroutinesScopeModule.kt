package com.thejawnpaul.gptinvestor.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
object CoroutinesScopeModule {
    @Singleton
    fun providesDefaultCoroutineScope(@DefaultDispatcher defaultDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + defaultDispatcher)
}
