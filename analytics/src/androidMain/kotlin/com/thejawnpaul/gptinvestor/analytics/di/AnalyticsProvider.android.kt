package com.thejawnpaul.gptinvestor.analytics.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.BuildConfig
import com.thejawnpaul.gptinvestor.analytics.composite.CompositeLogger
import com.thejawnpaul.gptinvestor.analytics.firebase.FirebaseLogger
import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun providesFirebaseLogger(firebase: AnalyticsLogger?): Module = module {
    single<FirebaseAnalytics> { Firebase.analytics }

    single<AnalyticsLogger>(named("FirebaseAnalytics")) {
        FirebaseLogger(firebaseAnalytics = get())
    }
}

actual fun providesMixpanelLogger(mixpanel: AnalyticsLogger?): Module = module {
    single<MixpanelAPI> {
        MixpanelAPI.getInstance(androidContext(), BuildConfig.MIXPANEL_TOKEN, true)
    }
    single<AnalyticsLogger>(named("MixpanelAnalytics")) {
        MixpanelLogger(mixpanel = get())
    }
}

actual fun providesCompositeLogger(): Module = module {
    single<AnalyticsLogger> {
        CompositeLogger(
            firebaseLogger = get(named("FirebaseAnalytics")),
            mixpanelLogger = get(named("MixpanelAnalytics"))
        )
    }
}