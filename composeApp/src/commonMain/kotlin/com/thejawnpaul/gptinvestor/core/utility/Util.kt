package com.thejawnpaul.gptinvestor.core.utility

expect fun Float.toCurrency(currencySymbol: String): String

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

expect fun Long.toReadable(): String

expect fun Long.formatAsRelativeDate(): String

expect fun Long.getHourAndMinute(): String
