package com.thejawnpaul.gptinvestor.analytics.mixpanel

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger

expect class MixpanelLogger : AnalyticsLogger {
    override fun logEvent(eventName: String, params: Map<String, Any>)

    override fun logViewEvent(screenName: String)
    override fun identifyUser(eventName: String, params: Map<String, Any>)

    override fun resetUser(eventName: String)
}