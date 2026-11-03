package com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchlistItemRemote(
    @SerialName("ticker") val ticker: String,
    @SerialName("company_name") val companyName: String = "",
    @SerialName("date_added") val dateAdded: String,
    @SerialName("locked") val locked: Boolean,
    @SerialName("logo_url") val logoUrl: String = "",
    @SerialName("current_price") val currentPrice: Double? = null,
    @SerialName("price_change") val priceChange: Double? = null,
    @SerialName("market") val market: String? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("currency_symbol") val currencySymbol: String? = null,
    @SerialName("digest_status") val digestStatus: String? = null
)
