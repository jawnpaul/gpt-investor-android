package com.thejawnpaul.gptinvestor.analytics.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.thejawnpaul.gptinvestor.analytics.BuildConfig
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module(includes = [AnalyticsBindingModule::class])
object AnalyticsModule {

    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }

    @Singleton
    fun provideMixpanelAPI(context: Context): MixpanelAPI {
        return MixpanelAPI.getInstance(context, BuildConfig.MIXPANEL_TOKEN, true)
    }
}