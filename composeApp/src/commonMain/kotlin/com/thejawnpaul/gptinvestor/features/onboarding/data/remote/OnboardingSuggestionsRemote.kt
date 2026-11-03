package com.thejawnpaul.gptinvestor.features.onboarding.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingSuggestionsRemote(
    @SerialName("market") val market: String? = null,
    @SerialName("stocks") val stocks: List<SuggestedStockRemote> = emptyList()
)

@Serializable
data class SuggestedStockRemote(
    @SerialName("ticker") val ticker: String,
    @SerialName("name") val name: String,
    @SerialName("logo_url") val logoUrl: String? = null
)
