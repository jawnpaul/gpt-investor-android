package com.thejawnpaul.gptinvestor.features.onboarding.domain.model

data class OnboardingSuggestions(val market: String?, val stocks: List<SuggestedStock>)

data class SuggestedStock(val ticker: String, val name: String, val logoUrl: String = "") {
    val displayTicker: String get() = ticker.substringBefore(".")
}
