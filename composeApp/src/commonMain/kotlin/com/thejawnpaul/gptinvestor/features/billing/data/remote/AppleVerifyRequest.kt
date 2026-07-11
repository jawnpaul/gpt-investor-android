package com.thejawnpaul.gptinvestor.features.billing.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppleVerifyRequest(@SerialName("original_transaction_id") val originalTransactionId: String)
