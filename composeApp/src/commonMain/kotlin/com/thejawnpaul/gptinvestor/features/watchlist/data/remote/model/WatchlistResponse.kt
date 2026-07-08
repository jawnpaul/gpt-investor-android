package com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchlistResponse(
    @SerialName("items") val items: List<WatchlistItemRemote>,
    @SerialName("page") val page: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("total_items") val totalItems: Int,
    @SerialName("cap") val cap: Int,
    @SerialName("plan") val plan: String
)
