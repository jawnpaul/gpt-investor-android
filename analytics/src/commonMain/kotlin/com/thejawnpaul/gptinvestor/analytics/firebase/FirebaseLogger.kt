package com.thejawnpaul.gptinvestor.analytics.firebase

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger

expect class FirebaseLogger : AnalyticsLogger {
    override fun logEvent(eventName: String, params: Map<String, Any>)

    override fun logViewEvent(screenName: String)
    override fun identifyUser(eventName: String, params: Map<String, Any>)

    override fun resetUser(eventName: String)
}