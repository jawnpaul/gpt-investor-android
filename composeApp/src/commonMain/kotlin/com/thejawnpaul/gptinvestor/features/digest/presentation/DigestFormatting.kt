package com.thejawnpaul.gptinvestor.features.digest.presentation

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class DigestSentiment { POSITIVE, NEUTRAL, NEGATIVE }

/** Maps the API's `sentiment`; null for older backends that don't send it, so no badge is shown. */
internal fun digestSentimentOf(value: String?): DigestSentiment? = when (value?.lowercase()) {
    "positive" -> DigestSentiment.POSITIVE
    "neutral" -> DigestSentiment.NEUTRAL
    "negative" -> DigestSentiment.NEGATIVE
    else -> null
}

private val DAY_NAMES = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val MONTH_NAMES = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

/** "2026-10-09" (the trading day a brief covers) -> "Fri, Oct 9". */
internal fun formatMarketDate(marketDate: String?): String? {
    val date = marketDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return null
    return "${DAY_NAMES[date.dayOfWeek.ordinal]}, ${MONTH_NAMES[date.month.ordinal]} ${date.day}"
}

/** ISO-8601 UTC instant -> local "Sat 7:01 PM"; null when missing or in the old RFC 1123 format. */
internal fun formatUpdatedAt(generatedAt: String?, timeZone: TimeZone): String? {
    val instant = generatedAt?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: return null
    val local = instant.toLocalDateTime(timeZone)
    val period = if (local.hour < 12) "AM" else "PM"
    val hour = when {
        local.hour == 0 -> 12
        local.hour > 12 -> local.hour - 12
        else -> local.hour
    }
    return "${DAY_NAMES[local.dayOfWeek.ordinal]} $hour:${local.minute.toString().padStart(2, '0')} $period"
}

/** 1.5 -> "+1.5%", -1.11 -> "-1.1%", 0.0 -> "0.0%". */
internal fun formatPriceChange(percent: Double): String {
    val tenths = (percent * 10).roundToInt()
    val sign = when {
        tenths > 0 -> "+"
        tenths < 0 -> "-"
        else -> ""
    }
    return "$sign${abs(tenths) / 10}.${abs(tenths) % 10}%"
}
