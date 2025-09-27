package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.api.createApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApi(ktorfit: Ktorfit): ApiService = ktorfit.createApiService()
}

@Module
@InstallIn(SingletonComponent::class)
object KoinHiltBridgeModule : KoinComponent {
    @Provides
    @Singleton
    fun provideKtorfitFromKoin(): Ktorfit {
        val ktorfit: Ktorfit by inject()
        return ktorfit
    }

    @Provides
    @Singleton
    fun providesJsonFromKoin(): Json {
        val json: Json by inject()
        return json
    }
}
