package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.couldn_t_load
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import org.jetbrains.compose.resources.stringResource

// Inline section error: the error banner with a small Retry, matching Home's movers error.
@Composable
fun HomeErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.couldn_t_load)
) {
    GiBanner(
        title = title,
        modifier = modifier,
        subtitle = message,
        tone = GiBannerTone.Error
    ) {
        GiButton(
            text = stringResource(Res.string.retry),
            onClick = onRetry,
            size = GiButtonSize.Small,
            variant = GiButtonVariant.Secondary
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeErrorCardPreview() {
    GPTInvestorTheme {
        HomeErrorCard(
            message = "Live prices are unavailable right now",
            onRetry = {},
            modifier = Modifier.padding(GiSpace.gutter)
        )
    }
}
