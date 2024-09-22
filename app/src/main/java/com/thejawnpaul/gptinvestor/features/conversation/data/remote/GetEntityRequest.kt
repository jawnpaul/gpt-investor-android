package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetEntityRequest(
    @field:Json(name = "query")val query: String
)

@JsonClass(generateAdapter = true)
data class GetEntityResponse(
    @field:Json(name = "entity")val entityList: List<String> = emptyList()
)
