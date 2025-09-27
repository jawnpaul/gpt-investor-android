package com.thejawnpaul.gptinvestor.features.company.domain.model

import kotlin.math.abs

data class TrendingCompany(val tickerSymbol: String, val companyName: String, val percentageChange: Float, val imageUrl: String) {
    val change = abs(percentageChange)
}
