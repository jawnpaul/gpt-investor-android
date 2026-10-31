package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketsResponse(@SerialName("markets") val markets: List<MarketRemote>)

@Serializable
data class MarketRemote(
    @SerialName("key") val key: String,
    @SerialName("name") val name: String,
    @SerialName("count") val count: Int
)
