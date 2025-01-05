package com.thejawnpaul.gptinvestor.features.toppick.domain.model

data class TopPick(
    val id: Long,
    val companyName: String,
    val ticker: String,
    val rationale: String,
    val metrics: List<String>,
    val risks: List<String>,
    val confidenceScore: Int
)
