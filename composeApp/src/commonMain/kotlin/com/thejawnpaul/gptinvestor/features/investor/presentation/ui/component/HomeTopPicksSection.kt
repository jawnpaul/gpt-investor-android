package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
                    HomeTopPickShimmer(modifier = Modifier.padding(horizontal = 0.dp))
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
            view.topPicks.take(2).forEachIndexed { index, pick ->
                HomeTopPickItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    pickPresentation = pick,
                    onClick = onClickPick
                )
                if (index != 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeTopPickShimmer(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ShimmerBox(
                        width = 40.dp,
                        height = 14.dp,
                        shape = RoundedCornerShape(4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(
                        width = 80.dp,
                        height = 10.dp,
                        shape = RoundedCornerShape(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth(),
                    height = 12.dp,
                    shape = RoundedCornerShape(4.dp)
                )
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    height = 12.dp,
                    shape = RoundedCornerShape(4.dp)
                )
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    height = 12.dp,
                    shape = RoundedCornerShape(4.dp)
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
