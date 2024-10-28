package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DefaultPromptRemote(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "label")val label: String,
    @field:Json(name = "query") val query: String
)
