package com.thejawnpaul.gptinvestor.features.billing.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyPurchaseRequest(
    @SerialName("purchase_token") val purchaseToken: String,
    @SerialName("subscription_id") val subscriptionId: String
)

