package com.thejawnpaul.gptinvestor.features.company.domain.model

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRemote
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyNews

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
)

fun CompanyFinancialsRemote.toDomainObject() = CompanyFinancials(
    open = open,
    high = high,
    low = low,
    close = close,
    volume = volume,
    marketCap = marketCap,
    currency = currency,
    news = news,
    historicalData = historicalData,
    balanceSheet = balanceSheet,
    financials = financials
)
