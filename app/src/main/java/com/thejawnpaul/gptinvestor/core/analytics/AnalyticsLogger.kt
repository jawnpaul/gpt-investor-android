package com.thejawnpaul.gptinvestor.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsLogger @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logDefaultPromptSelected(promptTitle: String, promptQuery: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, promptTitle)
            putString(FirebaseAnalytics.Param.ITEM_NAME, promptQuery)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "default_prompt")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }
}
