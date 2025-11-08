package com.thejawnpaul.gptinvestor.analytics.composite

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger

expect class CompositeLogger(
    firebaseLogger: AnalyticsLogger,
    mixpanelLogger: AnalyticsLogger
) : AnalyticsLogger {
    override fun logEvent(eventName: String, params: Map<String, Any>)

    override fun logViewEvent(screenName: String)
    override fun identifyUser(eventName: String, params: Map<String, Any>)

    override fun resetUser(eventName: String)
}