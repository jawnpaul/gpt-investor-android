package com.example.gptinvestor.features.company.domain.model

import com.example.gptinvestor.core.utility.getCurrencySymbol
import com.example.gptinvestor.core.utility.toCurrency
import com.example.gptinvestor.features.company.data.remote.model.CompanyNews
import com.example.gptinvestor.features.company.presentation.model.CompanyFinancialsPresentation

data class CompanyFinancials(
    val open: Float,
    val low: Float,
    val close: Float,
    val volume: Long,
    val high: Float,
    val currency: String,
    val marketCap: Long,
    val news: List<CompanyNews>,
    val historicalData: String,
    val balanceSheet: String,
    val financials: String
) {
    fun toPresentation(): CompanyFinancialsPresentation {
        return CompanyFinancialsPresentation(
            open = open.toCurrency(getCurrencySymbol(currency)),
            low = low.toCurrency(getCurrencySymbol(currency)),
            close = close.toCurrency(getCurrencySymbol(currency)),
            volume = volume.toString(),
            high = high.toCurrency(getCurrencySymbol(currency)),
            marketCap = marketCap.toString(),
            news = news.map { it.toPresentation() },
            balanceSheet = balanceSheet,
            historicalData = historicalData,
            financials = financials
        )
    }
}
