package com.thejawnpaul.gptinvestor.features.onboarding.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingSyncResponse(
    @SerialName("answers") val answers: Map<String, String?> = emptyMap(),
    @SerialName("asked") val asked: List<String> = emptyList(),
    @SerialName("added_stock") val addedStock: String? = null
)
