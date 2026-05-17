package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.HttpClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

@Module
@ComponentScan("com.thejawnpaul.gptinvestor.remote")
object RemoteModule {

    @Singleton
    fun provideHttpClient(
        @Provided tokenStorage: TokenStorage,
        @Provided unauthorizedCallback: UnauthorizedCallback
    ): HttpClient = KtorClientFactory.create(tokenStorage, unauthorizedCallback)
}