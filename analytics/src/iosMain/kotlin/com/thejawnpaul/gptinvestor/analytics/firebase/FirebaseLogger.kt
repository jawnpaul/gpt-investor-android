package com.thejawnpaul.gptinvestor.analytics.firebase

import cocoapods.FirebaseAnalytics.FIRAnalytics
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class FirebaseLogger : AnalyticsLogger {

    actual override fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        FIRAnalytics.logEventWithName(
            name = eventName.lowercase().replace(" ", "_"),
            parameters = params.mapKeys { it.key as Any? }
        )
    }

    actual override fun logViewEvent(screenName: String) {
    }

    actual override fun identifyUser(
        eventName: String,
        params: Map<String, Any>
    ) {
        FIRAnalytics.setUserID(params["user_id"].toString())
    }

    actual override fun resetUser(eventName: String) {
        FIRAnalytics.resetAnalyticsData()
    }
}