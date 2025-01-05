package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.ui.theme.GPTInvestorTheme

@Composable
fun TopPicks(modifier: Modifier = Modifier, state: TopPicksView, onClickRetry: () -> Unit, onClick: (id: Long) -> Unit, onClickSeeAll: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (state.loading) {
            // Text
            Text(
                text = stringResource(R.string.top_picks),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
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
                Text(stringResource(R.string.retry))
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
                    text = stringResource(R.string.top_picks),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(16.dp)
                )

                // Text
                Text(
                    text = stringResource(R.string.see_all),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable(onClick = onClickSeeAll)
                )
            }

            state.topPicks.take(2).forEach {
                SingleTopPickItem(pickPresentation = it, onClick = onClick)
                Spacer(modifier = Modifier.size(16.dp))
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
                                id = 1,
                                ticker = "AAPL",
                                companyName = "Apple",
                                rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, oh well it is just blah lorem ipsum ",
                                confidenceScore = 2,
                                metrics = emptyList(),
                                risks = emptyList()
                            ),
                            TopPickPresentation(
                                id = 2,
                                ticker = "AAPL",
                                companyName = "Microsoft corporation",
                                rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, oh well it is just blah lorem ipsum ",
                                confidenceScore = 2,
                                metrics = emptyList(),
                                risks = emptyList()
                            ),
                            TopPickPresentation(
                                id = 2,
                                ticker = "AAPL",
                                companyName = "Netflix",
                                rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, oh well it is just blah lorem ipsum ",
                                confidenceScore = 2,
                                metrics = emptyList(),
                                risks = emptyList()
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
