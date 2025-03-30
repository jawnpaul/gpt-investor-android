package com.thejawnpaul.gptinvestor.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.thejawnpaul.gptinvestor.analytics.Analytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLogger @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) :
    Analytics {
    override fun logDefaultPrompt(promptTitle: String, promptQuery: String) {
        val bundle = Bundle().apply {
            putString("prompt_title", promptTitle)
            putString("prompt_query", promptQuery)
        }
        firebaseAnalytics.logEvent("default_prompt", bundle)
        println("Log default prompt to firebase")
    }

    override fun logSelectedCompany(companyTicker: String) {
        val bundle = Bundle().apply {
            putString("company_ticker", companyTicker)
        }
        firebaseAnalytics.logEvent("company_selected", bundle)
        println("Log company to firebase $companyTicker")
    }

    override fun logCompanyIdentified(companyTicker: String) {
        val bundle = Bundle().apply {
            putString("company_ticker", companyTicker)
        }
        firebaseAnalytics.logEvent("company_identified", bundle)
    }

    override fun logShareEvent(contentType: String, contentName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            putString(FirebaseAnalytics.Param.ITEM_NAME, contentName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    override fun logSaveEvent(contentType: String, contentName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            putString(FirebaseAnalytics.Param.ITEM_NAME, contentName)
        }
        firebaseAnalytics.logEvent("save_event", bundle)
    }

}