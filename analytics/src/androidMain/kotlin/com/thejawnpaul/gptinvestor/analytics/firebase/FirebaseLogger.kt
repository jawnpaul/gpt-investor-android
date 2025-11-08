package com.thejawnpaul.gptinvestor.analytics.firebase

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger

actual class FirebaseLogger : AnalyticsLogger {

    private val firebaseAnalytics = Firebase.analytics

    actual override fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            bundle.putString(key, value.toString())
        }
        firebaseAnalytics.logEvent(eventName.lowercase().replace(" ", "_"), bundle)
    }

    actual override fun logViewEvent(screenName: String) {

    }

    actual override fun identifyUser(eventName: String, params: Map<String, Any>) {
        firebaseAnalytics.setUserId(params["user_id"].toString())
    }

    actual override fun resetUser(eventName: String) {
        firebaseAnalytics.resetAnalyticsData()
    }

}