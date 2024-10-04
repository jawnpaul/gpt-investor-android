package com.thejawnpaul.gptinvestor.features.company.presentation.model

import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange

data class CompanyPresentation(
    val ticker: String,
    val name: String,
    val logo: String,
    val summary: String,
    val price: Float,
    val priceChange: PriceChange? = null
)
