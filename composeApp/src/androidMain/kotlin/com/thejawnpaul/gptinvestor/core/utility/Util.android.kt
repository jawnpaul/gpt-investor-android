package com.thejawnpaul.gptinvestor.core.utility

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

actual fun Float.toCurrency(currencySymbol: String): String {
    val formatter = DecimalFormat("###,###,##0.00")
    val res = formatter.format(this.toDouble())
    return currencySymbol + res
}

actual fun Long.toReadable(): String = when {
    this >= 1_000_000_000_000 -> "%.1fT".format(this / 1e12)
    this >= 1_000_000_000 -> "%.1fB".format(this / 1e9)
    this >= 1_000_000 -> "%.1fM".format(this / 1e6)
    this >= 1_000 -> "%.1fK".format(this / 1e3)
    else -> this.toString()
}

actual fun Long.formatAsRelativeDate(): String {
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

actual fun Long.getHourAndMinute(): String {
    val date = Date(this)
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(date)
}
