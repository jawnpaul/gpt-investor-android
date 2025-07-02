package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideAnalyticsLogger(): AnalyticsLogger {
        val analyticsLogger: AnalyticsLogger by inject(AnalyticsLogger::class.java)
        return analyticsLogger
    }
}
