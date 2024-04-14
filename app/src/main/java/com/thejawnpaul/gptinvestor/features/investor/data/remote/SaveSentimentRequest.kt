package com.thejawnpaul.gptinvestor.features.investor.data.remote

import com.squareup.moshi.Json

data class SaveSentimentRequest(
    @field:Json(name = "ticker") val ticker: String,
    @field:Json(name = "sentiment") val sentiment: String
)
