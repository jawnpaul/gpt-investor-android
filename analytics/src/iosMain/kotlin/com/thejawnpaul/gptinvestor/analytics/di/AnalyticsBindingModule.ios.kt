package com.thejawnpaul.gptinvestor.analytics.di

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.composite.CompositeLogger
import com.thejawnpaul.gptinvestor.analytics.firebase.FirebaseLogger
import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelLogger
import org.koin.core.module.Module
import org.koin.dsl.module

actual val analyticsModule: Module
    get() = module {
        single<AnalyticsLogger>(firebaseAnalytics) { FirebaseLogger() }
        single<AnalyticsLogger>(mixpanelAnalytics) { MixpanelLogger() }
        single { CompositeLogger(get(firebaseAnalytics), get(mixpanelAnalytics)) }
    }