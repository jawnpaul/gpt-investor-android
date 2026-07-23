package com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model

import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchlistResponse(
    @SerialName("items") val items: List<WatchlistItemRemote>? = null,
    @SerialName("page") val page: Int? = null,
    @SerialName("page_size") val pageSize: Int? = null,
    @SerialName("total_items") val totalItems: Int? = null,
    @SerialName("cap") val cap: Int? = null,
    @SerialName("plan") val plan: String? = null,
    @SerialName("recommendations") val recommendedDigestCompany: List<RecommendedDigestCompany>? = null
)
