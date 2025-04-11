package com.thejawnpaul.gptinvestor.analytics

interface AnalyticsLogger {
    fun logEvent(eventName: String, params: Map<String, Any>)
    fun logViewEvent(screenName: String)
    fun identifyUser(eventName: String, params: Map<String, Any>)
    fun resetUser()
}
