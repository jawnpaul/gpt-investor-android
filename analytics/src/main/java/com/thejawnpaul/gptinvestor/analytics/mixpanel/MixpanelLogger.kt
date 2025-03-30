package com.thejawnpaul.gptinvestor.analytics.mixpanel

import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.thejawnpaul.gptinvestor.analytics.Analytics
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixpanelLogger @Inject constructor(private val mixpanel: MixpanelAPI) : Analytics {
    override fun logDefaultPrompt(promptTitle: String, promptQuery: String) {
        val props = JSONObject().apply {
            put("prompt_title", promptTitle)
            put("prompt_query", promptQuery)
        }
        mixpanel.track("Default Prompt Selected", props)
    }

    override fun logSelectedCompany(companyTicker: String) {
        val props = JSONObject().apply {
            put("company_ticker", companyTicker)
        }
        mixpanel.track("Company Selected", props)
    }

    override fun logCompanyIdentified(companyTicker: String) {
        val props = JSONObject().apply {
            put("company_ticker", companyTicker)
        }
        mixpanel.track("Company Selected", props)
    }

    override fun logShareEvent(contentType: String, contentName: String) {
        val props = JSONObject().apply {
            put("content_type", contentType)
            put("item_name", contentName)
        }
        mixpanel.track("Content Shared", props)
    }

    override fun logSaveEvent(contentType: String, contentName: String) {
        val props = JSONObject().apply {
            put("content_type", contentType)
            put("item_name", contentName)
        }
        mixpanel.track("Content Shared", props)
    }

}