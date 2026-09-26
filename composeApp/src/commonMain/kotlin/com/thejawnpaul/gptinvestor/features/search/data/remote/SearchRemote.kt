package com.thejawnpaul.gptinvestor.features.search.data.remote

import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.search.domain.model.ChipItem
import com.thejawnpaul.gptinvestor.features.search.domain.model.PromptItem
import com.thejawnpaul.gptinvestor.features.search.domain.model.SearchSection
import com.thejawnpaul.gptinvestor.features.search.domain.model.StockItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    @SerialName("query") val query: String,
    @SerialName("has_results") val hasResults: Boolean,
    @SerialName("sections") val sections: List<SearchSectionRemote>
)

@Serializable
data class SearchSectionRemote(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("title") val title: String? = null,
    @SerialName("clearable") val clearable: Boolean = false,
    @SerialName("query") val query: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("subtitle") val subtitle: String? = null,
    @SerialName("items") val items: List<SearchItemRemote> = emptyList()
)

@Serializable
data class SearchItemRemote(
    @SerialName("ticker") val ticker: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("query") val query: String? = null,
    @SerialName("key") val key: String? = null
)

@Serializable
data class ClearHistoryResponse(@SerialName("success") val success: Boolean)

fun SearchSectionRemote.toDomain(): SearchSection? = when (type) {
    "recent_list" -> {
        val stockItems = items.mapNotNull { it.toStockItem() }
        SearchSection.RecentList(
            id = id,
            title = title ?: "",
            clearable = clearable,
            items = stockItems
        )
    }
    "prompt_list" -> {
        val prompts = items.mapNotNull { item ->
            val label = item.label ?: return@mapNotNull null
            val query = item.query ?: return@mapNotNull null
            PromptItem(label = label, query = query)
        }
        SearchSection.PromptList(id = id, title = title ?: "", items = prompts)
    }
    "chip_list" -> {
        val chips = items.mapNotNull { item ->
            val key = item.key ?: return@mapNotNull null
            val label = item.label ?: return@mapNotNull null
            ChipItem(key = key, label = label)
        }
        SearchSection.ChipList(id = id, title = title ?: "", items = chips)
    }
    "stock_list" -> {
        val stockItems = items.mapNotNull { it.toStockItem() }
        SearchSection.StockList(id = id, title = title ?: "", items = stockItems)
    }
    "ask_gpt" -> {
        val q = query ?: return null
        SearchSection.AskGpt(id = id, title = title ?: "", query = q)
    }
    "empty_state" -> {
        val msg = message ?: return null
        SearchSection.EmptyState(id = id, message = msg, subtitle = subtitle ?: "")
    }
    else -> null
}

private fun SearchItemRemote.toStockItem(): StockItem? {
    val ticker = ticker ?: return null
    val name = name ?: return null
    return StockItem(ticker = ticker, name = name, logoUrl = logoUrl?.toHttpsUrl() ?: "")
}
