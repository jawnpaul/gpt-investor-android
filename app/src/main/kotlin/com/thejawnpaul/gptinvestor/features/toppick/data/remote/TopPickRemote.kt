package com.thejawnpaul.gptinvestor.features.toppick.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopPickRemote(
    @field:Json(name = "_id") val id: String,
    @field:Json(name = "company_name") val companyName: String,
    @field:Json(name = "ticker") val ticker: String,
    @field:Json(name = "rationale") val rationale: String,
    @field:Json(name = "key_metrics") val metrics: List<String>,
    @field:Json(name = "risks") val risks: List<String>,
    @field:Json(name = "confidence_score") val confidenceScore: Int,
    @field:Json(name = "date") val date: String,
    @field:Json(name = "price") val price: Float? = null,
    @field:Json(name = "change") val percentageChange: Float? = null,
    @field:Json(name = "logo_url") val imageUrl: String? = null
)
