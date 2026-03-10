package com.thejawnpaul.gptinvestor.core.di

import android.content.Context
import coil3.ImageLoader
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
object ImageModule {

    @Singleton
    fun provideImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context).build()
}
