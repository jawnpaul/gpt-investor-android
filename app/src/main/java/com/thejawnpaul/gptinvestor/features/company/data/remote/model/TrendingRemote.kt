package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrendingRemote(
    @field:Json(name = "ticker") val tickerSymbol: String,
    @field:Json(name = "change")val percentageChange: Float,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "logo") val logo: String
)
