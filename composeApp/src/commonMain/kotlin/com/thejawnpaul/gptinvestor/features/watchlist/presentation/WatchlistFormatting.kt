package com.thejawnpaul.gptinvestor.features.watchlist.presentation

import kotlin.math.abs
import kotlin.math.roundToLong

internal fun formatPrice(price: Double, currencySymbol: String): String {
    val cents = (abs(price) * 100).roundToLong()
    val dollars = (cents / 100).toString().reversed().chunked(3).joinToString(",").reversed()
    val sign = if (price < 0 && cents != 0L) "-" else ""
    return "$sign$currencySymbol$dollars.${(cents % 100).toString().padStart(2, '0')}"
}
