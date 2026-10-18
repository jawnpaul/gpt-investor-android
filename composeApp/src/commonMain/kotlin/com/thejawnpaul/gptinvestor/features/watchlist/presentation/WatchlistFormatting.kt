package com.thejawnpaul.gptinvestor.features.watchlist.presentation

import kotlin.math.abs
import kotlin.math.roundToLong

/** 382.7 -> "$382.70", 1234.5 -> "$1,234.50". Prices from the API are USD. */
internal fun formatUsdPrice(price: Double): String {
    val cents = (abs(price) * 100).roundToLong()
    val dollars = (cents / 100).toString().reversed().chunked(3).joinToString(",").reversed()
    val sign = if (price < 0 && cents != 0L) "-" else ""
    return "$sign$$dollars.${(cents % 100).toString().padStart(2, '0')}"
}
