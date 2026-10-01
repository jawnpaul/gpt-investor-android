package com.thejawnpaul.gptinvestor.features.digest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigestItemRemote(
    @SerialName("ticker") val ticker: String,
    @SerialName("company_name") val companyName: String,
    @SerialName("sentiment_change") val sentimentChange: String,
    @SerialName("locked") val locked: Boolean,
    @SerialName("summary") val summary: String? = null,
    @SerialName("key_event") val keyEvent: String? = null,
    @SerialName("generated_at") val generatedAt: String? = null
)
