package com.thejawnpaul.gptinvestor.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
object CoroutinesModule {
    @IoDispatcher
    @Factory
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Factory
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @DefaultDispatcher
    @Factory
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
