package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.runtime.Composable
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.TimePeriod
import com.thejawnpaul.gptinvestor.good_afternoon
import com.thejawnpaul.gptinvestor.good_evening
import com.thejawnpaul.gptinvestor.good_morning
import org.jetbrains.compose.resources.stringResource

@Composable
fun homeGreeting(timePeriod: TimePeriod, name: String?): String {
    val greeting = when (timePeriod) {
        TimePeriod.MORNING -> stringResource(Res.string.good_morning)
        TimePeriod.AFTERNOON -> stringResource(Res.string.good_afternoon)
        TimePeriod.EVENING -> stringResource(Res.string.good_evening)
    }
    val displayName = name?.takeIf { it.isNotBlank() }
    return if (displayName != null) "$greeting, $displayName" else greeting
}
