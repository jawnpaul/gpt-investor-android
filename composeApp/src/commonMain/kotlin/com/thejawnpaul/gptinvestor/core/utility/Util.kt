package com.thejawnpaul.gptinvestor.core.utility

fun Float.toCurrency(currencySymbol: String): String {
    val integerPart = toLong()
    val decimalPart = ((this - integerPart) * 100).toInt()
    val grouped = integerPart.toString().reversed().chunked(3).joinToString(",").reversed()
    return "$currencySymbol$grouped.${decimalPart.toString().padStart(2, '0')}"
}

fun Float.toTwoDecimalPlaces(): Float = kotlin.math.round(this * 100) / 100f

fun getCurrencySymbol(currency: String): String = when (currency.lowercase()) {
    "ngn" -> {
        "₦"
    }

    "usd" -> {
        "$"
    }

    "eur" -> {
        "€"
    }

    "gbp" -> {
        "£"
    }

    else -> {
        ""
    }
}

fun Long.toReadable(): String = when {
    this >= 1_000_000_000_000 -> "${roundToSingleDecimal(this / 1e12)}T"
    this >= 1_000_000_000 -> "${roundToSingleDecimal(this / 1e9)}B"
    this >= 1_000_000 -> "${roundToSingleDecimal(this / 1e6)}M"
    this >= 1_000 -> "${roundToSingleDecimal(this / 1e3)}K"
    else -> this.toString()
}

private fun roundToSingleDecimal(value: Double): String = (kotlin.math.round(value * 10) / 10).toString()
