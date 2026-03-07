package com.thejawnpaul.gptinvestor.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import org.koin.core.annotation.Singleton
import com.thejawnpaul.gptinvestor.analytics.di.FirebaseAnalytics as Firebase

@Singleton(binds = [AnalyticsLogger::class])
@Firebase
class FirebaseLogger (private val firebaseAnalytics: FirebaseAnalytics) :
    AnalyticsLogger {

    override fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            bundle.putString(key, value.toString())
        }
        firebaseAnalytics.logEvent(eventName.lowercase().replace(" ", "_"), bundle)
    }

    override fun logViewEvent(screenName: String) {

    }

    override fun identifyUser(eventName: String, params: Map<String, Any>) {
        firebaseAnalytics.setUserId(params["user_id"].toString())
    }

    override fun resetUser(eventName: String) {
        firebaseAnalytics.resetAnalyticsData()
    }

}