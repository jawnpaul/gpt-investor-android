package com.thejawnpaul.gptinvestor.features.company.presentation.model

import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company

data class CompanyPresentation(
    val ticker: String,
    val name: String,
    val logo: String,
    val summary: String,
    val price: Float,
    val priceChange: PriceChange
)


fun Company.toPresentation() = CompanyPresentation(
    ticker = ticker,
    name = name,
    logo = logo,
    summary = summary,
    price = price ?: 0.0F,
    priceChange = change
)