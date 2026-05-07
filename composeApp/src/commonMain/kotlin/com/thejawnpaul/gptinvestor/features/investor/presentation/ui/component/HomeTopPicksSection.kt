package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.couldn_t_fetch_picks
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.HomeTopPickItem
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeTopPicksSection(
    view: TopPicksView,
    onRetry: () -> Unit,
    onClickPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        view.loading -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(2) {
                    ShimmerBox(
                        modifier = Modifier.fillMaxWidth(),
                        height = 100.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        view.showError -> {
            HomeErrorCard(
                message = stringResource(Res.string.couldn_t_fetch_picks),
                onRetry = onRetry,
                modifier = modifier
            )
        }

        else -> {
            view.topPicks.take(2).forEach { pick ->
                HomeTopPickItem(
                    pickPresentation = pick,
                    onClick = onClickPick
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTopPicksSectionPreview() {
    val pick = TopPickPresentation(
        id = "1",
        ticker = "AAPL",
        companyName = "Apple Inc.",
        rationale = "Rationale for Apple Inc.",
        confidenceScore = 4,
        metrics = emptyList(),
        risks = emptyList(),
        isSaved = false,
        imageUrl = "",
        percentageChange = 2.5f,
        currentPrice = 150.0f
    )
    val view = TopPicksView(
        loading = false,
        topPicks = listOf(pick, pick.copy(id = "2", ticker = "MSFT", companyName = "Microsoft")),
        error = null
    )
    GPTInvestorTheme {
        Surface {
            HomeTopPicksSection(
                view = view,
                onRetry = {},
                onClickPick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTopPicksSectionLoadingPreview() {
    GPTInvestorTheme {
        Surface {
            HomeTopPicksSection(
                view = TopPicksView(loading = true),
                onRetry = {},
                onClickPick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTopPicksSectionErrorPreview() {
    GPTInvestorTheme {
        Surface {
            HomeTopPicksSection(
                view = TopPicksView(error = "Something went wrong"),
                onRetry = {},
                onClickPick = {}
            )
        }
    }
}
