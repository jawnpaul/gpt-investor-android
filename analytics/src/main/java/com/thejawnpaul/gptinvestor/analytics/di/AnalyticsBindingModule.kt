package com.thejawnpaul.gptinvestor.analytics.di

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.composite.CompositeLogger
import com.thejawnpaul.gptinvestor.analytics.firebase.FirebaseLogger
import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

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
    ): AnalyticsLogger

    @Binds
    @FirebaseAnalytics
    abstract fun bindFirebaseAnalytics(
        firebaseAnalyticsLogger: FirebaseLogger
    ): AnalyticsLogger

    @Binds
    abstract fun bindCompositeLogger(
        compositeLogger: CompositeLogger
    ): AnalyticsLogger
}