package com.thejawnpaul.gptinvestor.core.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val coroutinesModule = module {
    single(IosDispatcher) { Dispatchers.IO }

    single(MainDispatcher) { Dispatchers.Main }

    single(DefaultDispatcher) { Dispatchers.Default }
}
