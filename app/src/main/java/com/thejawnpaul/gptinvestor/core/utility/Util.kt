package com.thejawnpaul.gptinvestor.core.utility

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.serialization.json.Json

val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
}
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

fun Long.formatAsRelativeDate(): String {
    val currentDate = Date(this)
    val cal = Calendar.getInstance()
    cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // If timestamp is of today, return hh:mm format
    if (currentDate.after(cal.time)) {
        return "Today"
    }

    cal.add(Calendar.DATE, -1)
    return if (currentDate.after(cal.time)) {
        // If timestamp is of yesterday return "Yesterday"
        "Yesterday"
    } else {
        // If timestamp is older return the date, for eg. "Jul 05"
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(currentDate)
    }
}

fun Long.getHourAndMinute(): String {
    val date = Date(this)
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(date)
}
