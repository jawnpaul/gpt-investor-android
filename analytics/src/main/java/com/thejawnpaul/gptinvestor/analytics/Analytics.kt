package com.thejawnpaul.gptinvestor.analytics

interface Analytics {
    fun logDefaultPrompt(promptTitle: String, promptQuery: String)
    fun logSelectedCompany(companyTicker: String)
    fun logCompanyIdentified(companyTicker: String)
    fun logShareEvent(contentType: String, contentName: String)
    fun logSaveEvent(contentType: String, contentName: String)
    fun logTopPickSelected(companyTicker: String, companyName: String)

    enum class Provider {
        FIREBASE,
        MIXPANEL,
        ALL
    }
}