package com.thejawnpaul.gptinvestor.core.utility

import java.text.DecimalFormat

fun Float.toCurrency(currencySymbol: String): String {
    val formatter = DecimalFormat("###,###,##0.00")
    val res = formatter.format(this.toDouble())
    return currencySymbol + res
}

fun Float.toTwoDecimalPlaces(): Float {
    return kotlin.math.round(this * 100) / 100f
}

fun getCurrencySymbol(currency: String): String {
    return when (currency.lowercase()) {
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
}

fun Long.toReadable(): String {
    return when {
        this >= 1_000_000_000_000 -> "${"%.1f".format(this / 1e12)}T"
        this >= 1_000_000_000 -> "${"%.1f".format(this / 1e9)}B"
        this >= 1_000_000 -> "${"%.1f".format(this / 1e6)}M"
        this >= 1_000 -> "${"%.1f".format(this / 1e3)}K"
        else -> this.toString()
    }
}
