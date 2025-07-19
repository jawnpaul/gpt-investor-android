package com.thejawnpaul.gptinvestor.analytics.di

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.composite.CompositeLogger
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun providesFirebaseLogger(firebase: AnalyticsLogger?): Module = module {
    firebase?.let { logger ->
        single<AnalyticsLogger>(named("FirebaseAnalytics")) { logger }
    }
}

actual fun providesMixpanelLogger(mixpanel: AnalyticsLogger?): Module = module {
    mixpanel?.let { logger ->
        single<AnalyticsLogger>(named("MixpanelAnalytics")) { logger }
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