package com.thejawnpaul.gptinvestor.features.search.presentation.state

import com.thejawnpaul.gptinvestor.features.search.domain.model.SearchSection

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val sections: List<SearchSection> = emptyList(),
    val error: String? = null
)

sealed interface SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent
    data class OnStockClick(val ticker: String) : SearchEvent
    data class OnSectorClick(val key: String) : SearchEvent
    data class OnPromptClick(val query: String) : SearchEvent
    data class OnAskGptClick(val query: String) : SearchEvent
    data object OnClearHistory : SearchEvent
    data object OnRetry : SearchEvent
    data object OnBack : SearchEvent
}

sealed interface SearchAction {
    data object OnGoBack : SearchAction
    data class OnNavigateToCompany(val ticker: String) : SearchAction
    data class OnNavigateToConversation(val query: String) : SearchAction
    data class OnNavigateToSector(val sectorKey: String) : SearchAction
}
