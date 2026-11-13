package com.thejawnpaul.gptinvestor.features.news.presentation

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock
import com.thejawnpaul.gptinvestor.features.premium.domain.PREMIUM_SOURCE_WATCHLIST_FULL
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistFailure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.AddStocksToWatchlistUseCase
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.RemoveStocksFromWatchlistUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StockWatchState(
    val watched: Set<String> = emptySet(),
    val pending: Set<String> = emptySet(),
    val addingAll: Boolean = false
) {
    fun isWatched(ticker: String) = ticker in watched

    fun isPending(ticker: String) = ticker in pending

    fun showsRowSpinner(ticker: String) = isPending(ticker) && !addingAll

    fun unwatched(stocks: List<StoryStock>): List<String> = stocks.map { it.ticker }.filterNot { it in watched }
}

sealed interface StockWatchEvent {
    data class Added(val ticker: String) : StockWatchEvent
    data class AddedAll(val tickers: List<String>) : StockWatchEvent
    data class Removed(val ticker: String) : StockWatchEvent
    data object AddFailed : StockWatchEvent
    data class RemoveFailed(val ticker: String) : StockWatchEvent
    data object UndoFailed : StockWatchEvent
    data object SignInRequired : StockWatchEvent
}

class StockWatchController(
    private val addStocks: AddStocksToWatchlistUseCase,
    private val removeStocks: RemoveStocksFromWatchlistUseCase,
    private val premiumEventBus: PremiumEventBus,
    private val analyticsLogger: AnalyticsLogger,
    private val scope: CoroutineScope,
    private val source: String,
    private val isGuest: () -> Boolean,
    private val onEvent: suspend (StockWatchEvent) -> Unit
) {
    private val _state = MutableStateFlow(StockWatchState())
    val state = _state.asStateFlow()

    fun sync(stocks: List<StoryStock>) {
        _state.update { current ->
            val settled = stocks.filterNot { current.isPending(it.ticker) }
            current.copy(
                watched = current.watched - settled.map { it.ticker }.toSet() +
                    settled.filter { it.isOnWatchlist }.map { it.ticker }
            )
        }
    }

    fun toggle(ticker: String) {
        if (guarded() || _state.value.isPending(ticker)) return
        if (_state.value.isWatched(ticker)) remove(listOf(ticker), silent = false) else add(listOf(ticker), all = false)
    }

    fun addAll(stocks: List<StoryStock>) {
        val targets = _state.value.unwatched(stocks).filterNot { _state.value.isPending(it) }
        if (guarded() || targets.isEmpty()) return
        add(targets, all = true)
    }

    fun undo(tickers: List<String>) = remove(tickers, silent = true)

    private fun guarded(): Boolean {
        if (!isGuest()) return false
        emit(StockWatchEvent.SignInRequired)
        return true
    }

    private fun add(tickers: List<String>, all: Boolean) {
        _state.update { it.copy(pending = it.pending + tickers, addingAll = it.addingAll || all) }
        addStocks(tickers) { result ->
            _state.update { it.copy(pending = it.pending - tickers.toSet(), addingAll = it.addingAll && !all) }
            result
                .onSuccess { batch ->
                    _state.update { it.copy(watched = it.watched + batch.watched) }
                    batch.added.forEach { ticker ->
                        analyticsLogger.logEvent(
                            eventName = "stock-added-to-watchlist",
                            params = mapOf("ticker" to ticker, "source" to source)
                        )
                    }
                    when {
                        !all -> emit(StockWatchEvent.Added(tickers.first()))
                        batch.added.isNotEmpty() -> emit(StockWatchEvent.AddedAll(batch.added))
                    }
                }
                .onFailure { failure ->
                    if (failure is WatchlistFailure.WatchlistFull) {
                        premiumEventBus.requestPremium(
                            source = PREMIUM_SOURCE_WATCHLIST_FULL,
                            watchlistCap = failure.cap,
                            ticker = tickers.singleOrNull(),
                            watchlistUsed = failure.used
                        )
                    } else {
                        emit(StockWatchEvent.AddFailed)
                    }
                }
        }
    }

    private fun remove(tickers: List<String>, silent: Boolean) {
        _state.update { it.copy(pending = it.pending + tickers) }
        removeStocks(tickers) { result ->
            _state.update { it.copy(pending = it.pending - tickers.toSet()) }
            result
                .onSuccess { removed ->
                    _state.update { it.copy(watched = it.watched - removed.toSet()) }
                    removed.forEach { ticker ->
                        analyticsLogger.logEvent(
                            eventName = "stock-removed-from-watchlist",
                            params = mapOf("ticker" to ticker, "source" to source)
                        )
                    }
                    if (!silent) emit(StockWatchEvent.Removed(tickers.first()))
                }
                .onFailure {
                    emit(if (silent) StockWatchEvent.UndoFailed else StockWatchEvent.RemoveFailed(tickers.first()))
                }
        }
    }

    private fun emit(event: StockWatchEvent) {
        scope.launch { onEvent(event) }
    }
}
