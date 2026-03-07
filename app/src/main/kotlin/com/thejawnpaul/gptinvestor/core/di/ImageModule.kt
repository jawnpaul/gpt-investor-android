package com.thejawnpaul.gptinvestor.core.di

import android.content.Context
import coil.ImageLoader
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
object ImageModule {

    @Singleton
    fun provideImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}
