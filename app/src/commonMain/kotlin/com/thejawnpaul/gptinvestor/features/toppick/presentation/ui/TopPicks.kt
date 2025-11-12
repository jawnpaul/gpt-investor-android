package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.linkMedium
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.retry
import gptinvestor.app.generated.resources.see_all
import gptinvestor.app.generated.resources.top_picks
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopPicks(modifier: Modifier = Modifier, state: TopPicksView, onClickRetry: () -> Unit, onClick: (id: String) -> Unit, onClickSeeAll: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (state.loading) {
            // Text
            Text(
                text = stringResource(Res.string.top_picks),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(16.dp)
            )

            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (state.showError) {
            Button(
                onClick = onClickRetry,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                enabled = !state.loading
            ) {
                Text(stringResource(Res.string.retry))
            }
        }

        if (state.topPicks.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Text
                Text(
                    text = stringResource(Res.string.top_picks),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(16.dp)
                )

                // Text
                Text(
                    text = stringResource(Res.string.see_all),
                    style = MaterialTheme.typography.linkMedium,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable(onClick = onClickSeeAll),
                    textDecoration = TextDecoration.Underline
                )
            }

            OutlinedCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                val numberOfItemsToShow = 2
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    state.topPicks.take(numberOfItemsToShow).forEachIndexed { index, presentation ->
                        HomeTopPickItem(
                            modifier = Modifier.padding(vertical = 16.dp),
                            pickPresentation = presentation,
                            onClick = onClick
                        )
                        if (index < numberOfItemsToShow - 1) {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TopPicksPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                TopPicks(
                    state = TopPicksView(
                        loading = false,
                        error = null,
                        topPicks = listOf(
                            TopPickPresentation(
                                id = "1",
                                ticker = "AAPL",
                                companyName = "Apple",
                                rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, oh well it is just blah lorem ipsum ",
                                confidenceScore = 2,
                                metrics = emptyList(),
                                risks = emptyList(),
                                isSaved = true,
                                imageUrl = "",
                                percentageChange = 0.0f,
                                currentPrice = 0.0f
                            ),
                            TopPickPresentation(
                                id = "2",
                                ticker = "AAPL",
                                companyName = "Microsoft corporation",
                                rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, oh well it is just blah lorem ipsum ",
                                confidenceScore = 2,
                                metrics = emptyList(),
                                risks = emptyList(),
                                isSaved = false,
                                imageUrl = "",
                                percentageChange = 0.0f,
                                currentPrice = 0.0f
                            ),
                            TopPickPresentation(
                                id = "2",
                                ticker = "AAPL",
                                companyName = "Netflix",
                                rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, oh well it is just blah lorem ipsum ",
                                confidenceScore = 2,
                                metrics = emptyList(),
                                risks = emptyList(),
                                isSaved = true,
                                imageUrl = "",
                                percentageChange = 0.0f,
                                currentPrice = 0.0f
                            )
                        )
                    ),
                    onClick = {},
                    onClickRetry = {},
                    onClickSeeAll = {
                    }
                )
            }
        }
    }
}
