package com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistFailure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.PopularStockUiModel
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.WatchlistLoadError
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.WatchlistUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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

@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
    private val companyRepository: ICompanyRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    private val _uiState = MutableStateFlow(WatchlistUiState(isLoading = true))
    private val retryRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // Loads when the screen starts collecting, and again after any add/remove anywhere in the app
    // (e.g. company detail) or a retry, so the tab never shows a stale list.
    private val reloads = merge(watchlistRepository.changes, retryRequests)
        .onStart { emit(Unit) }
        .mapLatest { loadWatchlist() }

    val uiState: StateFlow<WatchlistUiState> = combine(_uiState, reloads) { state, _ -> state }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WatchlistUiState(isLoading = true)
        )

    private val _actions = MutableSharedFlow<WatchlistAction>()
    val actions = _actions.asSharedFlow()

    init {
        analyticsLogger.logEvent("watchlist-screen-viewed", emptyMap())
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
            WatchlistEvent.RetryWatchlist -> retryRequests.tryEmit(Unit)
            WatchlistEvent.OnSearchClick -> {
                val triggerState = if (_uiState.value.watchlistItems.isEmpty()) "empty" else "content"
                analyticsLogger.logEvent(
                    eventName = "watchlist-search-tapped",
                    params = mapOf("trigger_state" to triggerState)
                )
                emitAction(WatchlistAction.NavigateToSearch)
            }
            WatchlistEvent.OnSignInClick -> emitAction(WatchlistAction.NavigateToSignUp)
            WatchlistEvent.OnBrowseTopPicksClick -> {
                analyticsLogger.logEvent("watchlist-browse-top-picks-tapped", emptyMap())
                emitAction(WatchlistAction.NavigateToTopPicks)
            }
        }
    }

    private suspend fun loadWatchlist() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        watchlistRepository.getWatchlist().fold(
            { failure ->
                _uiState.update {
                    if (failure == WatchlistFailure.SignUpRequired) {
                        it.copy(isLoading = false, requiresSignUp = true)
                    } else {
                        val error = if (failure == Failure.NetworkConnection) {
                            WatchlistLoadError.NETWORK
                        } else {
                            WatchlistLoadError.SERVER
                        }
                        it.copy(isLoading = false, error = error)
                    }
                }
            },
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
                if (data.items.isEmpty() && popularStocks.isNotEmpty()) {
                    viewModelScope.launch { loadPopularStockLogos(popularStocks.map { it.company.ticker }) }
                }
            }
        )
    }

    // The API's suggestions carry no logos; fetch them so the empty state matches the list's real logos.
    private suspend fun loadPopularStockLogos(tickers: List<String>) {
        companyRepository.getCompanyLogos(tickers).onSuccess { logos ->
            _uiState.update { state ->
                state.copy(
                    popularStocks = state.popularStocks.map { stock ->
                        stock.copy(logoUrl = logos[stock.company.ticker] ?: stock.logoUrl)
                    }
                )
            }
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
    data object OnSignInClick : WatchlistEvent
}

sealed interface WatchlistAction {
    data object NavigateToSearch : WatchlistAction
    data object NavigateToTopPicks : WatchlistAction
    data object NavigateToSignUp : WatchlistAction
    data class NavigateToCompanyDetail(val ticker: String) : WatchlistAction
    data class ShowAddedMessage(val ticker: String) : WatchlistAction
    data class ShowRemovedMessage(val ticker: String) : WatchlistAction
    data class ShowRemoveErrorMessage(val ticker: String) : WatchlistAction
}
