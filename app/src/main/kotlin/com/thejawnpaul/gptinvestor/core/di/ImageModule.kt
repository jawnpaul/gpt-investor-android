package com.thejawnpaul.gptinvestor.core.di

import coil.ImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val imageModule = module {
    single<ImageLoader> {
        ImageLoader.Builder(androidContext())
            .crossfade(true)
            .build()
    }
}
