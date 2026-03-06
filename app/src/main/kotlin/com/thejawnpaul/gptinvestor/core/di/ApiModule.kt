package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApi(client: HttpClient): KtorApiService = KtorApiService(client)
}

