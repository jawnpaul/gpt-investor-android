package com.thejawnpaul.gptinvestor.analytics.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MixpanelAnalytics

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseAnalytics

@Module
@ComponentScan("com.thejawnpaul.gptinvestor.analytics")
object AnalyticsModule