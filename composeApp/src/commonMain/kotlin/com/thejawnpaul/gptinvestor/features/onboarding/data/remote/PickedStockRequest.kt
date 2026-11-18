package com.thejawnpaul.gptinvestor.features.onboarding.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PickedStockRequest(@SerialName("ticker") val ticker: String)
