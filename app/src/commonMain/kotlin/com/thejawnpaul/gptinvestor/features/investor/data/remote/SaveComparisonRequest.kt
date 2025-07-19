package com.thejawnpaul.gptinvestor.features.investor.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveComparisonRequest(
    @SerialName("androidMain") val mainTicker: String,
    @SerialName("other") val otherTicker: String,
    @SerialName("gemini_text") val geminiText: String
)

@Serializable
data class DefaultSaveResponse(
    @SerialName("_id") val id: String
)
