package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.api.GeminiApi
import com.thejawnpaul.gptinvestor.core.api.GeminiApiImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun providesGeminiApi(api: GeminiApi?): Module = module {
    single<GeminiApi> { GeminiApiImpl(get()) }
}
