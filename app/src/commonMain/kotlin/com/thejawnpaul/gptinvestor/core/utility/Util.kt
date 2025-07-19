package com.thejawnpaul.gptinvestor.core.utility

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
}
fun Float.toCurrency(currencySymbol: String): String {
    val decimal = BigDecimal.fromFloat(this)
    val formatter = PlatformNumberFormatter()
    val res = formatter.format(decimal, 2)
    return currencySymbol + res
}

fun Float.toTwoDecimalPlaces(): Float {
    return round(this * 100) / 100f
}

fun getCurrencySymbol(currency: String): String {
    return when (currency.lowercase()) {
        "ngn" -> "₦"
        "usd" -> "$"
        "eur" -> "€"
        "gbp" -> "£"
        else -> ""
    }
}

fun Long.toReadable(): String {
    return when {
        this >= 1_000_000_000_000 -> "${(this / 1e12).format()}T"
        this >= 1_000_000_000 -> "${(this / 1e9).format()}B"
        this >= 1_000_000 -> "${(this / 1e6).format()}M"
        this >= 1_000 -> "${(this / 1e3).format()}K"
        else -> this.toString()
    }
}

private fun Double.format(): Double {
    val integerPart = this.toInt()
    val decimalPart = this - integerPart
    val decimalAsInt = decimalPart.times(10).roundToInt()
    val formattedDecimal = decimalAsInt.toDouble() / 10
    return integerPart + formattedDecimal
}

@OptIn(ExperimentalTime::class)
fun Long.formatAsRelativeDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    val localDate = instant.toLocalDateTime(timeZone).date
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val yesterday = today.minus(1, DateTimeUnit.DAY)
    return when(localDate) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> {
            val dateFormatter = LocalDate.Format {
                monthName(MonthNames.ENGLISH_ABBREVIATED)
                char(' ')
                day()
                char(',')
                char(' ')
                year()
            }
            localDate.format(dateFormatter)
        }
    }
}

@OptIn(ExperimentalTime::class)
fun Long.getHourAndMinute(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    val localTime = instant.toLocalDateTime(timeZone).time
    val timeFormatter = LocalTime.Format {
        amPmHour()
        char(':')
        minute()
        char(' ')
        amPmMarker(am = "AM", pm = "PM")
    }
    return localTime.format(timeFormatter)
}
