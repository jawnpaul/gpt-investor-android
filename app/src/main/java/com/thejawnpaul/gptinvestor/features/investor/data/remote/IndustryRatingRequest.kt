package com.thejawnpaul.gptinvestor.features.investor.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class IndustryRatingRequest(val industry: String, val sector: String, val rating: String)
