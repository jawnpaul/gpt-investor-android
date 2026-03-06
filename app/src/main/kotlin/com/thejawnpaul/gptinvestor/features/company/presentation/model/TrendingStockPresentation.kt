package com.thejawnpaul.gptinvestor.features.company.presentation.model

data class TrendingStockPresentation(
    val companyName: String,
    val tickerSymbol: String,
    val imageUrl: String,
    val percentageChange: Float
)
