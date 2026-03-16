package com.thejawnpaul.gptinvestor.core.utility

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSOrderedDescending
import platform.Foundation.NSOrderedSame
import platform.Foundation.dateWithTimeIntervalSince1970

actual fun Float.toCurrency(currencySymbol: String): String {
    val formatter = NSNumberFormatter()
    formatter.numberStyle = NSNumberFormatterDecimalStyle
    formatter.minimumFractionDigits = 2u
    formatter.maximumFractionDigits = 2u
    val res = formatter.stringFromNumber(NSNumber(this.toDouble())) ?: ""
    return currencySymbol + res
}

actual fun Long.toReadable(): String = when {
    this >= 1_000_000_000_000 -> {
        val formatter = NSNumberFormatter()
        formatter.maximumFractionDigits = 1u
        val res = formatter.stringFromNumber(NSNumber(this / 1e12)) ?: ""
        "${res}T"
    }
    this >= 1_000_000_000 -> {
        val formatter = NSNumberFormatter()
        formatter.maximumFractionDigits = 1u
        val res = formatter.stringFromNumber(NSNumber(this / 1e9)) ?: ""
        "${res}B"
    }
    this >= 1_000_000 -> {
        val formatter = NSNumberFormatter()
        formatter.maximumFractionDigits = 1u
        val res = formatter.stringFromNumber(NSNumber(this / 1e6)) ?: ""
        "${res}M"
    }
    this >= 1_000 -> {
        val formatter = NSNumberFormatter()
        formatter.maximumFractionDigits = 1u
        val res = formatter.stringFromNumber(NSNumber(this / 1e3)) ?: ""
        "${res}K"
    }
    else -> this.toString()
}

actual fun Long.formatAsRelativeDate(): String {
    val date = NSDate.dateWithTimeIntervalSince1970(this.toDouble() / 1000.0)
    val calendar = NSCalendar.currentCalendar

    val todayStartOfDay = calendar.startOfDayForDate(NSDate())

    // Compare date with today's start of day
    if (calendar.compareDate(date, toDate = todayStartOfDay, toUnitGranularity = NSCalendarUnitDay) ==
        NSOrderedDescending
    ) {
        return "Today"
    }

    // Check for Yesterday
    val yesterdayStartOfDay = calendar.dateByAddingUnit(NSCalendarUnitDay, -1, toDate = todayStartOfDay, options = 0u)
    if (yesterdayStartOfDay != null &&
        calendar.compareDate(date, toDate = yesterdayStartOfDay, toUnitGranularity = NSCalendarUnitDay) == NSOrderedSame
    ) {
        return "Yesterday"
    }

    val formatter = NSDateFormatter()
    formatter.dateFormat = "MMM dd, yyyy"
    return formatter.stringFromDate(date)
}

actual fun Long.getHourAndMinute(): String {
    val date = NSDate.dateWithTimeIntervalSince1970(this.toDouble() / 1000.0)
    val formatter = NSDateFormatter()
    formatter.dateFormat = "hh:mm a"
    return formatter.stringFromDate(date)
}
