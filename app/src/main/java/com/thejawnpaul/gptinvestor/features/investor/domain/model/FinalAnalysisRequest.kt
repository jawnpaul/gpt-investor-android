package com.thejawnpaul.gptinvestor.features.investor.domain.model

data class FinalAnalysisRequest(
    val ticker: String,
    val comparison: String? = null,
    val sentiment: String? = null,
    val analystRating: String? = null,
    val industryRating: String? = null
)
