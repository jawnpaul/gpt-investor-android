package com.thejawnpaul.gptinvestor.features.investor.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SaveSentimentRequest(
    @field:Json(name = "ticker") val ticker: String,
    @field:Json(name = "sentiment") val sentiment: String
)
