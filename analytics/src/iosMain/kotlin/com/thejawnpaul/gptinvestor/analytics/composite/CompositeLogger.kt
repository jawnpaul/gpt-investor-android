package com.thejawnpaul.gptinvestor.analytics.composite

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger

class CompositeLogger(
    private val firebaseLogger: AnalyticsLogger,
    private val mixpanelLogger: AnalyticsLogger
) : AnalyticsLogger {
    override fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        firebaseLogger.logEvent(eventName = eventName, params = params)
        mixpanelLogger.logEvent(eventName = eventName, params = params)
    }

    override fun logViewEvent(screenName: String) {
        firebaseLogger.logViewEvent(screenName = screenName)
        mixpanelLogger.logViewEvent(screenName = screenName)
    }

    override fun identifyUser(
        eventName: String,
        params: Map<String, Any>
    ) {
        firebaseLogger.identifyUser(eventName = eventName, params = params)
        mixpanelLogger.identifyUser(eventName = eventName, params = params)
    }

    override fun resetUser(eventName: String) {
        firebaseLogger.resetUser(eventName)
        mixpanelLogger.resetUser(eventName)
    }
}