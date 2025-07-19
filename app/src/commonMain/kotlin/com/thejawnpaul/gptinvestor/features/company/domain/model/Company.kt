package com.thejawnpaul.gptinvestor.features.company.domain.model

import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange

data class Company(
    val ticker: String,
    val name: String,
    val summary: String,
    val logo: String,
    val price: Float? = null,
    val change: PriceChange
)
