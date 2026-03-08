package com.thejawnpaul.gptinvestor.analytics.mixpanel

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.di.MixpanelAnalytics
import org.koin.core.annotation.Singleton

@Singleton(binds = [AnalyticsLogger::class])
@MixpanelAnalytics
actual class MixpanelLogger(private val provider: MixpanelProvider) : AnalyticsLogger {

    actual override fun logEvent(eventName: String, params: Map<String, Any>) {
        provider.logEvent(eventName, params)
    }

    actual override fun logViewEvent(screenName: String) {
        provider.logViewEvent(screenName)
    }

    actual override fun identifyUser(eventName: String, params: Map<String, Any>) {
        val userId = params["user_id"]?.toString() ?: return
        provider.identifyUser(userId, params)
    }

    actual override fun resetUser(eventName: String) {
        provider.resetUser()
    }
}
