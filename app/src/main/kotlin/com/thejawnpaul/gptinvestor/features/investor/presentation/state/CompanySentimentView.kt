package com.thejawnpaul.gptinvestor.features.investor.presentation.state

data class CompanySentimentView(
    val loading: Boolean = false,
    val result: String? = null,
    val error: String? = null
)
