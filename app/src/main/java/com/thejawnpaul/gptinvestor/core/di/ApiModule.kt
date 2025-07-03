package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton
import org.koin.java.KoinJavaComponent.inject

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesHttpClient(): HttpClient {
        val client: HttpClient by inject(HttpClient::class.java)
        return client
    }

    @Provides
    @Singleton
    fun provideAnalyticsLogger(): AnalyticsLogger {
        val analyticsLogger: AnalyticsLogger by inject(AnalyticsLogger::class.java)
        return analyticsLogger
    }
}
