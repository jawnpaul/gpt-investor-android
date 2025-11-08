package com.thejawnpaul.gptinvestor.analytics.mixpanel

import cocoapods.Mixpanel.Mixpanel
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.BuildKonfig
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class MixpanelLogger : AnalyticsLogger {

    private val mixpanel = Mixpanel.sharedInstanceWithToken(
        apiToken = BuildKonfig.MIXPANEL_TOKEN,
        trackAutomaticEvents = true
    )

    actual override fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        mixpanel.track(eventName, params.mapKeys { it as Any? })
    }

    actual override fun logViewEvent(screenName: String) {
    }

    actual override fun identifyUser(
        eventName: String,
        params: Map<String, Any>
    ) {
        val userIdKey = "user_id"
        mixpanel.track(eventName)
        mixpanel.identify(params[userIdKey].toString(), true)
        mixpanel.people.set(params.filterKeys { it != userIdKey }.mapKeys { it as Any? })
    }

    actual override fun resetUser(eventName: String) {
        mixpanel.track(eventName)
        mixpanel.reset()
    }
}