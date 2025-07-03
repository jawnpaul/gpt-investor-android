package com.thejawnpaul.gptinvestor.features.investor.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SaveSentimentRequest(
    val ticker: String,
    val sentiment: String
)
