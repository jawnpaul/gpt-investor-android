package com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchlistBatchRequest(@SerialName("tickers") val tickers: List<String>)

@Serializable
data class AddWatchlistBatchResponse(
    @SerialName("added") val added: List<String> = emptyList(),
    @SerialName("already_watched") val alreadyWatched: List<String> = emptyList()
)

@Serializable
data class RemoveWatchlistBatchResponse(@SerialName("removed") val removed: List<String> = emptyList())
