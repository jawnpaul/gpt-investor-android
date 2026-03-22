package com.thejawnpaul.gptinvestor.features.billing.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyPurchaseResponse(
    @SerialName("success") val success: Boolean? = null,
    @SerialName("message") val message: String? = null
)
