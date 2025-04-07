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
        when (eventName.lowercase()) {
            "sign up" -> {

            }

            "log in" -> {

            }

            "log out" -> {

            }

            "delete account" -> {

            }

            else -> {
                val props = JSONObject()
                params.forEach { (key, value) ->
                    props.put(key, value)
                }
                mixpanel.track(eventName, props)
            }
        }
    }

    override fun logViewEvent(screenName: String) {

    }

}