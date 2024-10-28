package com.thejawnpaul.gptinvestor.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsLogger @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logDefaultPromptSelected(promptTitle: String, promptQuery: String) {
        val bundle = Bundle().apply {
            putString("prompt_title", promptTitle)
            putString("prompt_query", promptQuery)
        }
        firebaseAnalytics.logEvent("default_prompt", bundle)
    }

    fun logCompanySelected(companyTicker: String) {
        val bundle = Bundle().apply {
            putString("company_ticker", companyTicker)
        }
        firebaseAnalytics.logEvent("company_selected", bundle)
    }

    fun logCompanyIdentified(companyTicker: String) {
        val bundle = Bundle().apply {
            putString("company_ticker", companyTicker)
        }
        firebaseAnalytics.logEvent("company_identified", bundle)
    }
}
