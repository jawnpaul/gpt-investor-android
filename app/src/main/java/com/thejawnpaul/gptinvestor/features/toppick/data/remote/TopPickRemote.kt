package com.thejawnpaul.gptinvestor.features.toppick.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopPickRemote(
    @field:Json(name = "company_name") val companyName: String,
    @field:Json(name = "ticker") val ticker: String,
    @field:Json(name = "rationale") val rationale: String,
    @field:Json(name = "key_metrics") val metrics: List<String>,
    @field:Json(name = "risks") val risks: List<String>,
    @field:Json(name = "confidence_score") val confidenceScore: Int
)