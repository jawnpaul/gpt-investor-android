package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddToWaitlistRequest(
    @field:Json(name = "user_id")val userId: String,
    @field:Json(name = "model_id")val modelId: String,
    @field:Json(name = "reasons")val reasons: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AddToWaitlistResponse(
    @field:Json(name = "message")val status: String
)
