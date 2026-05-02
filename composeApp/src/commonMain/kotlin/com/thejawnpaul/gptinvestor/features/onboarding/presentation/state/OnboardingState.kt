package com.thejawnpaul.gptinvestor.features.onboarding.presentation.state

import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief

sealed interface BriefView {
    data object Loading : BriefView
    data class Success(val brief: CompanyBrief) : BriefView
    data class Error(val message: String) : BriefView
}

data class SuggestedStock(val ticker: String, val name: String, val logoUrl: String = "")

data class OnboardingUiState(
    val currentScreen: Int = 0,
    val selectedTicker: String? = null,
    val selectedCompanyName: String? = null,
    val briefView: BriefView = BriefView.Loading,
    val searchQuery: String = "",
    val suggestedStocks: List<SuggestedStock> = defaultSuggestedStocks()
)

private fun defaultSuggestedStocks() = listOf(
    SuggestedStock("AAPL", "Apple"),
    SuggestedStock("TSLA", "Tesla"),
    SuggestedStock("NVDA", "Nvidia"),
    SuggestedStock("MSFT", "Microsoft"),
    SuggestedStock("AMZN", "Amazon"),
    SuggestedStock("GOOGL", "Google")
)
