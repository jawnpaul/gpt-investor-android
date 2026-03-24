package com.thejawnpaul.gptinvestor.analytics.firebase

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics
import org.koin.core.annotation.Singleton

@Singleton(binds = [AnalyticsLogger::class])
@com.thejawnpaul.gptinvestor.analytics.di.FirebaseAnalytics
class FirebaseLogger : AnalyticsLogger {

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    override fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        firebaseAnalytics.logEvent(eventName.lowercase().replace(" ", "_"), params)
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