package com.thejawnpaul.gptinvestor.features.company.domain.model

import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation

data class Company(val ticker: String, val name: String, val summary: String, val logo: String, val price: Float? = null, val change: PriceChange) {
    fun toPresentation() = CompanyPresentation(
        ticker = ticker,
        name = name,
        logo = logo,
        summary = summary,
        price = price ?: 0.0F,
        priceChange = change
    )
}
