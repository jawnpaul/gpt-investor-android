package com.thejawnpaul.gptinvestor.analytics.composite

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.di.FirebaseAnalytics
import com.thejawnpaul.gptinvestor.analytics.di.MixpanelAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompositeLogger @Inject constructor(
    @FirebaseAnalytics private val firebaseLogger: AnalyticsLogger,
    @MixpanelAnalytics private val mixpanelLogger: AnalyticsLogger
) :
    AnalyticsLogger {

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

    override fun resetUser() {
        firebaseLogger.resetUser()
        mixpanelLogger.resetUser()
    }
}