package com.thejawnpaul.gptinvestor.features.news.presentation

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val DAY_NAMES = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val MONTH_NAMES = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private fun Long.local(timeZone: TimeZone): LocalDateTime = Instant.fromEpochSeconds(this).toLocalDateTime(timeZone)

internal fun newsClock(epochSeconds: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val local = epochSeconds.local(timeZone)
    return "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
}

internal fun newsWeekday(epochSeconds: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    DAY_NAMES[epochSeconds.local(timeZone).dayOfWeek.ordinal]

internal fun newsDayMonth(epochSeconds: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val local = epochSeconds.local(timeZone)
    return "${local.day} ${MONTH_NAMES[local.month.ordinal]}"
}
