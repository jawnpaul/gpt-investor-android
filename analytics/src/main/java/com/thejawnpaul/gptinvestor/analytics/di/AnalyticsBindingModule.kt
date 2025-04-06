package com.thejawnpaul.gptinvestor.analytics.di

import com.thejawnpaul.gptinvestor.analytics.Analytics
import com.thejawnpaul.gptinvestor.analytics.composite.CompositeLogger
import com.thejawnpaul.gptinvestor.analytics.firebase.FirebaseLogger
import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MixpanelAnalytics

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseAnalytics

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsBindingModule {

    @Binds
    @MixpanelAnalytics
    abstract fun bindMixpanelAnalytics(
        mixpanelAnalyticsLogger: MixpanelLogger
    ): Analytics

    @Binds
    @FirebaseAnalytics
    abstract fun bindFirebaseAnalytics(
        firebaseAnalyticsLogger: FirebaseLogger
    ): Analytics

    companion object {
        /**
         * Provides a default analytics logger with all loggers enabled
         */
        @Provides
        @Singleton
        fun provideDefaultAnalyticsLogger(builder: CompositeLogger.Builder): Analytics {
            return builder.withAllLoggers().build()
        }
    }
}