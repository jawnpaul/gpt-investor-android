package com.thejawnpaul.gptinvestor.analytics.mixpanel

import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixpanelLogger @Inject constructor(private val mixpanel: MixpanelAPI) : AnalyticsLogger {

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