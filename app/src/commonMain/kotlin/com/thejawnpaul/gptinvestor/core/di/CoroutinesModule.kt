package com.thejawnpaul.gptinvestor.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val providesCoroutinesModule = module {
    single<CoroutineDispatcher>(named<IoDispatcher>()) { Dispatchers.IO }

    single<CoroutineDispatcher>(named<MainDispatcher>()) { Dispatchers.Main }

    single<CoroutineDispatcher>(named<DefaultDispatcher>()) { Dispatchers.Default }

    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named<DefaultDispatcher>()))
    }

}
