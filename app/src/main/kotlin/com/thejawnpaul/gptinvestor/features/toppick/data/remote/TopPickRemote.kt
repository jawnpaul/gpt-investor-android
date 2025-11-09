package com.thejawnpaul.gptinvestor.features.toppick.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopPickRemote(
    @SerialName("_id") val id: String,
    @SerialName("company_name") val companyName: String,
    val ticker: String,
    val rationale: String,
    @SerialName("key_metrics") val metrics: List<String>,
    val risks: List<String>,
    @SerialName("confidence_score") val confidenceScore: Int,
    val date: String,
    val price: Float? = null,
    @SerialName("change") val percentageChange: Float? = null,
    @SerialName("logo_url") val imageUrl: String? = null
)
