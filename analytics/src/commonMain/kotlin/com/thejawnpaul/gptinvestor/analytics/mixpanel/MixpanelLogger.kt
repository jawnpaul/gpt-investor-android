package com.thejawnpaul.gptinvestor.analytics.mixpanel

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.di.MixpanelAnalytics
import org.koin.core.annotation.Singleton

@Singleton(binds = [AnalyticsLogger::class])
@MixpanelAnalytics
expect class MixpanelLogger : AnalyticsLogger {

    override fun logEvent(eventName: String, params: Map<String, Any>)

    override fun logViewEvent(screenName: String)
    override fun identifyUser(eventName: String, params: Map<String, Any>)

    override fun resetUser(eventName: String)

}