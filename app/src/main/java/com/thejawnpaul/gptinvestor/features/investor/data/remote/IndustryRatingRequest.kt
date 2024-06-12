package com.thejawnpaul.gptinvestor.features.investor.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IndustryRatingRequest(
    @field:Json(name = "industry")val industry: String,
    @field:Json(name = "sector")val sector: String,
    @field:Json(name = "rating")val rating: String
)
