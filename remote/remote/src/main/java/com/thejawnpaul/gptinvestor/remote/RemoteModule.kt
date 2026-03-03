package com.thejawnpaul.gptinvestor.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        tokenStorage: TokenStorage,
        unauthorizedCallback: UnauthorizedCallback
    ): HttpClient = KtorClientFactory.create(tokenStorage, unauthorizedCallback)
}