package com.thejawnpaul.gptinvestor.features.toppick.domain.model

data class TopPick(
    val id: String,
    val companyName: String,
    val ticker: String,
    val rationale: String,
    val metrics: List<String>,
    val risks: List<String>,
    val confidenceScore: Int,
    val isSaved: Boolean
)
