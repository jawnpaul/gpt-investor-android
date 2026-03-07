package com.thejawnpaul.gptinvestor.analytics.mixpanel

import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.analytics.di.MixpanelAnalytics
import org.json.JSONObject
import org.koin.core.annotation.Singleton

@Singleton(binds = [AnalyticsLogger::class])
@MixpanelAnalytics
class MixpanelLogger (private val mixpanel: MixpanelAPI) : AnalyticsLogger {

    override fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        val props = JSONObject()
        params.forEach { (key, value) ->
            props.put(key, value)
        }
        mixpanel.track(eventName, props)
    }

    override fun logViewEvent(screenName: String) {

    }

    override fun identifyUser(eventName: String, params: Map<String, Any>) {
        mixpanel.track(eventName)
        val props = JSONObject()
        params.forEach { (key, value) ->
            if (key != "user_id") {
                props.put(key, value)
            }
        }
        mixpanel.identify(params["user_id"].toString(), true)
        mixpanel.people.set(props)
    }

    override fun resetUser(eventName: String) {
        mixpanel.track(eventName)
        mixpanel.reset()
    }

}