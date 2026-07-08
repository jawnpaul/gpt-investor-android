package com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddWatchlistRequest(
    @SerialName("ticker") val ticker: String
)
