package com.thejawnpaul.gptinvestor.features.premium.presentation.state

data class PremiumSheetState(
    val isVisible: Boolean = false,
    val isPurchasing: Boolean = false,
    val watchlistCap: Int? = null,
    val ticker: String? = null,
    val watchlistUsed: Int? = null
) {
    val slotsUsed: Int?
        get() = watchlistCap?.let { cap -> watchlistUsed?.coerceIn(0, cap) ?: cap }
}
