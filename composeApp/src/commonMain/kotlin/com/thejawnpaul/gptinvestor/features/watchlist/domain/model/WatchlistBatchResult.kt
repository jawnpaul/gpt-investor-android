package com.thejawnpaul.gptinvestor.features.watchlist.domain.model

data class WatchlistBatchResult(val added: List<String>, val alreadyWatched: List<String>) {
    val watched: List<String> get() = added + alreadyWatched
}
