package com.thejawnpaul.gptinvestor.analytics.di

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import org.koin.core.module.Module

expect fun providesFirebaseLogger(firebase: AnalyticsLogger?): Module

expect fun providesMixpanelLogger(mixpanel: AnalyticsLogger?): Module

expect fun providesCompositeLogger(): Module