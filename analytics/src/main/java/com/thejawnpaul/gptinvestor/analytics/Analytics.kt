package com.thejawnpaul.gptinvestor.analytics

interface Analytics {
    fun logEvent(eventName: String, params: Map<String, Any>)
    fun logViewEvent(screenName: String)

    enum class Provider {
        FIREBASE,
        MIXPANEL,
        ALL
    }
}