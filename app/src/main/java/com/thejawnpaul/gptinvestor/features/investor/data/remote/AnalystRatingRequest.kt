package com.thejawnpaul.gptinvestor.features.investor.data.remote

import com.squareup.moshi.Json

data class AnalystRatingRequest(
    @field:Json(name = "ticker") val ticker: String
)

data class AnalystRatingResponse(
    @field:Json(name = "rating") val rating: String
)
