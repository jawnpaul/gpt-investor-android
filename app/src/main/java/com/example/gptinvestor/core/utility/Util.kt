package com.example.gptinvestor.core.utility

import java.text.DecimalFormat

fun Float.toCurrency(currencySymbol: String): String {
    val formatter = DecimalFormat("###,###,##0.00")
    val res = formatter.format(this.toDouble())
    return currencySymbol + res
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
