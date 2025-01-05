package com.thejawnpaul.gptinvestor.features.toppick.presentation.model

data class TopPickPresentation(
    val id: Long,
    val companyName: String,
    val ticker: String,
    val rationale: String,
    val metrics: List<String>,
    val risks: List<String>,
    val confidenceScore: Int
)
