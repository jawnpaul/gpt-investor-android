package com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.PopularStockUiModel
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.WatchlistUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

private val recommendationColors = listOf(
    Color(0xFF6366F1),
    Color(0xFF10B981),
    Color(0xFFF59E0B),
    Color(0xFFEF4444),
    Color(0xFF3B82F6),
    Color(0xFF8B5CF6)
)

@KoinViewModel
class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<WatchlistAction>()
    val actions = _actions.asSharedFlow()

    init {
        analyticsLogger.logEvent("watchlist-screen-viewed", emptyMap())
        getWatchlist()
    }

    fun onEvent(event: WatchlistEvent) {
        when (event) {
            is WatchlistEvent.OnWatchClick -> addToWatchlist(event.ticker)
            is WatchlistEvent.OnRemoveClick -> removeFromWatchlist(event.ticker)
            is WatchlistEvent.OnItemClick -> {
                analyticsLogger.logEvent(
                    eventName = "watchlist-item-tapped",
                    params = mapOf("ticker" to event.ticker)
                )
                emitAction(WatchlistAction.NavigateToCompanyDetail(event.ticker))
            }
            WatchlistEvent.RetryWatchlist -> getWatchlist()
            WatchlistEvent.OnSearchClick -> {
                val triggerState = if (_uiState.value.watchlistItems.isEmpty()) "empty" else "content"
                analyticsLogger.logEvent(
                    eventName = "watchlist-search-tapped",
                    params = mapOf("trigger_state" to triggerState)
                )
                emitAction(WatchlistAction.NavigateToSearch)
            }
            WatchlistEvent.OnBrowseTopPicksClick -> {
                analyticsLogger.logEvent("watchlist-browse-top-picks-tapped", emptyMap())
                emitAction(WatchlistAction.NavigateToTopPicks)
            }
        }
    }

    private fun getWatchlist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            watchlistRepository.getWatchlist().fold(
                { _uiState.update { it.copy(isLoading = false, error = "failed") } },
                { data ->
                    val popularStocks = data.recommendations.mapIndexed { index, company ->
                        PopularStockUiModel(
                            company = company,
                            color = recommendationColors[index % recommendationColors.size]
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            watchlistItems = data.items,
                            popularStocks = popularStocks,
                            error = null
                        )
                    }
                }
            )
        }
    }

    private fun addToWatchlist(ticker: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            watchlistRepository.addStockToWatchList(ticker).fold(
                { _uiState.update { it.copy(isLoading = false) } },
                {
                    analyticsLogger.logEvent(
                        eventName = "stock-added-to-watchlist",
                        params = mapOf("ticker" to ticker, "source" to "popular_suggestion")
                    )
                    _uiState.update { it.copy(isLoading = false) }
                    emitAction(WatchlistAction.ShowAddedMessage(ticker))
                    getWatchlist()
                }
            )
        }
    }

    private fun removeFromWatchlist(ticker: String) {
        viewModelScope.launch {
            watchlistRepository.removeFromWatchlist(ticker).fold(
                { emitAction(WatchlistAction.ShowRemoveErrorMessage(ticker)) },
                {
                    analyticsLogger.logEvent(
                        eventName = "stock-removed-from-watchlist",
                        params = mapOf("ticker" to ticker)
                    )
                    emitAction(WatchlistAction.ShowRemovedMessage(ticker))
                    getWatchlist()
                }
            )
        }
    }

    private fun emitAction(action: WatchlistAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

sealed interface WatchlistEvent {
    data class OnWatchClick(val ticker: String) : WatchlistEvent
    data class OnRemoveClick(val ticker: String) : WatchlistEvent
    data class OnItemClick(val ticker: String) : WatchlistEvent
    data object RetryWatchlist : WatchlistEvent
    data object OnSearchClick : WatchlistEvent
    data object OnBrowseTopPicksClick : WatchlistEvent
}

sealed interface WatchlistAction {
    data object NavigateToSearch : WatchlistAction
    data object NavigateToTopPicks : WatchlistAction
    data class NavigateToCompanyDetail(val ticker: String) : WatchlistAction
    data class ShowAddedMessage(val ticker: String) : WatchlistAction
    data class ShowRemovedMessage(val ticker: String) : WatchlistAction
    data class ShowRemoveErrorMessage(val ticker: String) : WatchlistAction
}
