package com.example.gptinvestor.features.investor.data.remote

import com.squareup.moshi.Json

data class SaveComparisonRequest(
    @field:Json(name = "main") val mainTicker: String,
    @field:Json(name = "other") val otherTicker: String,
    @field:Json(name = "gemini_text") val geminiText: String
)

data class DefaultSaveResponse(
    @field:Json(name = "_id") val id: String
)
