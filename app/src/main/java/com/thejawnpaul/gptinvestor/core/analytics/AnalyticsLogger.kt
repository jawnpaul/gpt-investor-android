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

    fun logCompanySaved(companyTicker: String) {
    }

    fun logShareEvent(contentType: String, contentName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            putString(FirebaseAnalytics.Param.ITEM_NAME, contentName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    fun logSaveEvent(contentType: String, contentName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            putString(FirebaseAnalytics.Param.ITEM_NAME, contentName)
        }
        firebaseAnalytics.logEvent("save_event", bundle)
    }
}
