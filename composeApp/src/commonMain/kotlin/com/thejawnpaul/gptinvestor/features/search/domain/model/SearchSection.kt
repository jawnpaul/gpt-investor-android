package com.thejawnpaul.gptinvestor.features.search.domain.model

sealed interface SearchSection {
    data class RecentList(val id: String, val title: String, val clearable: Boolean, val items: List<StockItem>) :
        SearchSection

    data class PromptList(val id: String, val title: String, val items: List<PromptItem>) : SearchSection

    data class ChipList(val id: String, val title: String, val items: List<ChipItem>) : SearchSection

    data class StockList(val id: String, val title: String, val items: List<StockItem>) : SearchSection

    data class AskGpt(val id: String, val title: String, val query: String) : SearchSection

    data class EmptyState(val id: String, val message: String, val subtitle: String) : SearchSection
}

data class StockItem(val ticker: String, val name: String, val logoUrl: String)
data class PromptItem(val label: String, val query: String)
data class ChipItem(val key: String, val label: String)
