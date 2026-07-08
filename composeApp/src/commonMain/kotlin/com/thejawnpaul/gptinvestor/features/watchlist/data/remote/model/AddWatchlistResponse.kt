package com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddWatchlistResponse(
    @SerialName("ticker") val ticker: String? = null,
    @SerialName("company_name") val companyName: String? = null,
    @SerialName("date_added") val dateAdded: String? = null,
    @SerialName("logo_url") val logoUrl: String? = null,
)
