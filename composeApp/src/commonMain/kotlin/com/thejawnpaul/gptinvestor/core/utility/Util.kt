package com.thejawnpaul.gptinvestor.core.utility

import kotlin.math.abs

expect fun Float.toCurrency(currencySymbol: String): String

/** Upgrades an HTTP image URL to HTTPS. Required for iOS ATS compliance. No-op if already HTTPS. */
fun String.toHttpsUrl(): String = replace("http://", "https://")

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

fun relativeTime(epochSec: Long, now: Long): String {
    if (epochSec <= 0L) return ""
    val diff = abs(now - epochSec)
    return when {
        diff < 60L -> "just now"
        diff < 3_600L -> "${diff / 60L}m ago"
        diff < 86_400L -> "${diff / 3_600L}h ago"
        diff < 172_800L -> "Yesterday"
        diff < 604_800L -> "${diff / 86_400L} days ago"
        else -> "${diff / 604_800L}w ago"
    }
}

expect fun Long.toReadable(): String

expect fun Long.formatAsRelativeDate(): String

expect fun Long.getHourAndMinute(): String
