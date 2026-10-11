package com.thejawnpaul.gptinvestor.features.watchlist.presentation.state

import androidx.compose.ui.graphics.Color
import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistItem

data class WatchlistUiState(
    val popularStocks: List<PopularStockUiModel> = emptyList(),
    val watchlistItems: List<WatchlistItem> = emptyList(),
    val isLoading: Boolean = false,
    val requiresSignUp: Boolean = false,
    val error: WatchlistLoadError? = null
)

enum class WatchlistLoadError { NETWORK, SERVER }

data class PopularStockUiModel(val company: RecommendedDigestCompany, val color: Color, val logoUrl: String? = null)
