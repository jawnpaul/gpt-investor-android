package com.thejawnpaul.gptinvestor.analytics

interface AnalyticsLogger {
    fun logEvent(eventName: String, params: Map<String, Any>)
    fun logViewEvent(screenName: String)
}
