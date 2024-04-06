package com.example.gptinvestor.features.investor.data.remote

import com.squareup.moshi.Json

data class IndustryRatingRequest(
    @field:Json(name = "industry")val industry: String,
    @field:Json(name = "sector")val sector: String,
    @field:Json(name = "rating")val rating: String
)