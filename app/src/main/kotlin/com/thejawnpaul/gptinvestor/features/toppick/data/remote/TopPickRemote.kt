package com.thejawnpaul.gptinvestor.features.toppick.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopPickRemote(
    @SerialName("_id") val id: String,
    @SerialName("company_name") val companyName: String,
    @SerialName("ticker") val ticker: String,
    @SerialName("rationale") val rationale: String,
    @SerialName("key_metrics") val metrics: List<String>,
    @SerialName("risks") val risks: List<String>,
    @SerialName("confidence_score") val confidenceScore: Int,
    @SerialName("date") val date: String,
    @SerialName("price") val price: Float? = null,
    @SerialName("change") val percentageChange: Float? = null,
    @SerialName("logo_url") val imageUrl: String? = null
)
