package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany

data class DailyDigestView(
    val loading: Boolean = false,
    val status: DailyDigestStatus = DailyDigestStatus.READY,
    val stocks: List<StockDigestPresentation> = emptyList(),
    val suggestedStocks: List<RecommendedDigestCompany> = emptyList(),
    val isStale: Boolean = false,
    val lastUpdated: String? = null,
    val lockedStocksCount: Int = 0,
    val lockedStocksSummary: String? = null,
    val isPremium: Boolean = false,
    val addingTickers: Set<String> = emptySet(),
    val addedTickers: Set<String> = emptySet(),
    val nextHourLabel: String = ""
)

enum class DailyDigestStatus { EMPTY, PENDING, READY }

data class StockDigestPresentation(
    val ticker: String,
    val name: String,
    val summary: String,
    val isImproved: Boolean = false,
    val keyEvent: String? = null
)
