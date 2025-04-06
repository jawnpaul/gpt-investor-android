package com.thejawnpaul.gptinvestor.analytics.firebase

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.thejawnpaul.gptinvestor.analytics.Analytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLogger @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) :
    Analytics {

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
                val bundle = Bundle()
                params.forEach { (key, value) ->
                    bundle.putString(key, value.toString())
                }
                firebaseAnalytics.logEvent(eventName, bundle)
            }
        }
    }

    override fun logViewEvent(screenName: String) {

    }

}