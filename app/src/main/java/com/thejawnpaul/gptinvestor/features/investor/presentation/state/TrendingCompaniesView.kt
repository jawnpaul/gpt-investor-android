package com.thejawnpaul.gptinvestor.features.investor.presentation.state

import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation

data class TrendingCompaniesView(
    val loading: Boolean = false,
    val companies: List<TrendingStockPresentation> = emptyList(),
    val error: String? = null
)
