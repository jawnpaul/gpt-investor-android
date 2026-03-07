package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import io.ktor.client.HttpClient
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
object ApiModule {

    @Singleton
    fun provideApi(client: HttpClient): KtorApiService = KtorApiService(client)
}

