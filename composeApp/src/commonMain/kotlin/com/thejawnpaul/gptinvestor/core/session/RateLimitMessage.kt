package com.thejawnpaul.gptinvestor.core.session

import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.rate_limit_try_again_hours
import com.thejawnpaul.gptinvestor.rate_limit_try_again_minutes
import com.thejawnpaul.gptinvestor.rate_limit_try_again_shortly
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString

suspend fun rateLimitRetryMessage(retryAfterSeconds: Long?): String {
    val seconds = retryAfterSeconds?.takeIf { it > 0 } ?: return getString(Res.string.rate_limit_try_again_shortly)
    val minutes = ((seconds + 59) / 60).toInt()
    if (minutes < 60) return getPluralString(Res.plurals.rate_limit_try_again_minutes, minutes, minutes)
    val hours = ((seconds + 1800) / 3600).toInt()
    return getPluralString(Res.plurals.rate_limit_try_again_hours, hours, hours)
}
