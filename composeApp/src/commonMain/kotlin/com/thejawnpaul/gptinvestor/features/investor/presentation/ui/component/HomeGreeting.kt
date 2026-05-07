package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.TimePeriod
import com.thejawnpaul.gptinvestor.good_afternoon
import com.thejawnpaul.gptinvestor.good_evening
import com.thejawnpaul.gptinvestor.good_morning
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeGreeting(timePeriod: TimePeriod, name: String?, modifier: Modifier = Modifier) {
    val greeting = when (timePeriod) {
        TimePeriod.MORNING -> stringResource(Res.string.good_morning)
        TimePeriod.AFTERNOON -> stringResource(Res.string.good_afternoon)
        TimePeriod.EVENING -> stringResource(Res.string.good_evening)
    }
    val displayName = name?.takeIf { it.isNotBlank() }
    val greetingText = if (displayName != null) "$greeting, $displayName" else greeting

    Text(
        modifier = modifier,
        text = greetingText,
        style = MaterialTheme.typography.bodyLarge,
        color = LocalGPTInvestorColors.current.textColors.secondary50
    )
}

@PreviewLightDark
@Composable
private fun HomeGreetingPreview() {
    GPTInvestorTheme {
        Surface {
            HomeGreeting(
                timePeriod = TimePeriod.MORNING,
                name = "John Doe"
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeGreetingNoNamePreview() {
    GPTInvestorTheme {
        Surface {
            HomeGreeting(
                timePeriod = TimePeriod.AFTERNOON,
                name = null
            )
        }
    }
}
