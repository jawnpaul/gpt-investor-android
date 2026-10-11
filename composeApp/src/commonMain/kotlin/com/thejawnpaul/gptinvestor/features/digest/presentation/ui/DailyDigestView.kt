package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany
import com.thejawnpaul.gptinvestor.features.digest.presentation.DigestSentiment

data class DailyDigestView(
    val loading: Boolean = false,
    val status: DailyDigestStatus = DailyDigestStatus.READY,
    val stocks: List<StockDigestPresentation> = emptyList(),
    val suggestedStocks: List<RecommendedDigestCompany> = emptyList(),
    val isStale: Boolean = false,
    val briefDateLabel: String? = null,
    val updatedLabel: String? = null,
    val nextDigestLabel: String? = null,
    val lockedStocksCount: Int = 0,
    val lockedStocksSummary: String? = null,
    val isPremium: Boolean = true,
    val addingTickers: Set<String> = emptySet(),
    val addedTickers: Set<String> = emptySet(),
    val lockedStocks: List<LockedStockDigestPresentation> = emptyList(),
    val trackedTickers: List<String> = emptyList()
)

enum class DailyDigestStatus { EMPTY, PENDING, READY }

data class StockDigestPresentation(
    val ticker: String,
    val name: String,
    val summary: String,
    val keyEvent: String? = null,
    val sentiment: DigestSentiment? = null,
    val priceChange: Double? = null,
    val updatedLabel: String? = null
)

data class LockedStockDigestPresentation(
    val ticker: String,
    val name: String,
    val sentiment: DigestSentiment? = null,
    val priceChange: Double? = null
)
