package com.thejawnpaul.gptinvestor.features.investor.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class AnalystRatingRequest(val ticker: String)

@Serializable
data class AnalystRatingResponse(val rating: String)
