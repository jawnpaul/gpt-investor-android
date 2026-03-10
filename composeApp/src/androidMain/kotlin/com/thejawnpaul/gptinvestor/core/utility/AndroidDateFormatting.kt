package com.thejawnpaul.gptinvestor.core.utility

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.formatAsRelativeDate(): String {
    val currentDate = Date(this)
    val cal = Calendar.getInstance()
    cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    if (currentDate.after(cal.time)) {
        return "Today"
    }

    cal.add(Calendar.DATE, -1)
    return if (currentDate.after(cal.time)) {
        "Yesterday"
    } else {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(currentDate)
    }
}

fun Long.getHourAndMinute(): String {
    val date = Date(this)
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(date)
}
