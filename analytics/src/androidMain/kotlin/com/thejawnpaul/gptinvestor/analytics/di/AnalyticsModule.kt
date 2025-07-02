package com.thejawnpaul.gptinvestor.analytics.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.BuildConfig
import com.thejawnpaul.gptinvestor.analytics.firebase.FirebaseLogger
import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val providesAnalyticsModule = module {

    single<FirebaseAnalytics> { Firebase.analytics }

    single<MixpanelAPI> {
        MixpanelAPI.getInstance(androidContext(), BuildConfig.MIXPANEL_TOKEN, true)
    }

    single<AnalyticsLogger>(named("FirebaseAnalytics")) {
        FirebaseLogger(firebaseAnalytics = get())
    }
    single<AnalyticsLogger>(named("MixpanelAnalytics")) {
        MixpanelLogger(mixpanel = get())
    }
    single<AnalyticsLogger> {
        com.thejawnpaul.gptinvestor.analytics.composite.CompositeLogger(
            firebaseLogger = get(named("FirebaseAnalytics")),
            mixpanelLogger = get(named("MixpanelAnalytics"))
        )
    }
}