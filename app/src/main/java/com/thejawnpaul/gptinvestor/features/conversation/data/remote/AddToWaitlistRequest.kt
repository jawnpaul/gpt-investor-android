package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddToWaitlistRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("model_id") val modelId: String,
    val reasons: List<String> = emptyList()
)

@Serializable
data class AddToWaitlistResponse(
    @SerialName("message") val status: String
)
