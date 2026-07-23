package com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.WatchlistUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class WatchlistViewModel(private val watchlistRepository: WatchlistRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<WatchlistAction>()
    val actions = _actions.asSharedFlow()

    init {
        getWatchlist()
    }

    fun onEvent(event: WatchlistEvent) {
        when (event) {
            is WatchlistEvent.OnWatchClick -> {
                addToWatchlist(event.ticker)
            }
            WatchlistEvent.OnSearchClick -> {
                emitAction(WatchlistAction.NavigateToSearch)
            }
            WatchlistEvent.OnBrowseTopPicksClick -> {
                emitAction(WatchlistAction.NavigateToTopPicks)
            }
        }
    }

    private fun getWatchlist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            watchlistRepository.getWatchlist().fold(
                { failure ->
                    _uiState.update { it.copy(isLoading = false, error = failure.toString()) }
                },
                { items ->
                    _uiState.update { it.copy(isLoading = false, watchlistItems = items) }
                }
            )
        }
    }

    private fun addToWatchlist(ticker: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            watchlistRepository.addStockToWatchList(ticker).fold(
                { failure ->
                    _uiState.update { it.copy(isLoading = false, error = failure.toString()) }
                },
                {
                    _uiState.update { it.copy(isLoading = false) }
                    emitAction(WatchlistAction.ShowMessage("Added $ticker to watchlist"))
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
    data object OnSearchClick : WatchlistEvent
    data object OnBrowseTopPicksClick : WatchlistEvent
}

sealed interface WatchlistAction {
    data object NavigateToSearch : WatchlistAction
    data object NavigateToTopPicks : WatchlistAction
    data class ShowMessage(val message: String) : WatchlistAction
}
