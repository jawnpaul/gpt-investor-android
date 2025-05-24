package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeUiState
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.TopPicks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, state: HomeUiState, onAction: (HomeAction) -> Unit, onEvent: (HomeEvent) -> Unit) {
    // Home Screen
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
        ) {
            // App bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onAction(HomeAction.OnMenuClick) }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_menu),
                        contentDescription = null
                    )
                }

                Text(
                    text = stringResource(R.string.gpt_investor),
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                ThemeDropdown(
                    modifier = Modifier,
                    onClick = {
                        onEvent(HomeEvent.ChangeTheme(it))
                    },
                    options = listOf("Light", "Dark", "System"),
                    selectedOption = state.theme ?: "Dark"
                )
            }

            // Trending
            TrendingStockList(
                modifier = Modifier.padding(vertical = 16.dp),
                state = state.trendingCompaniesView,
                onClick = {
                    onAction(HomeAction.OnGoToCompanyDetail(it))
                },
                onClickRetry = { onEvent(HomeEvent.RetryTrendingStocks) }
            )

            QuestionInput(
                modifier = Modifier,
                onSendClicked = {
                    onEvent(HomeEvent.SendClick)
                },
                hint = stringResource(R.string.ask_me_a_question),
                onTextChange = {
                    onEvent(HomeEvent.ChatInputChanged(it))
                },
                text = state.chatInput ?: ""
            )

            // Top Picks
            TopPicks(modifier = Modifier, state = state.topPicksView, onClick = {
                onAction(HomeAction.OnGoToTopPickDetail(it))
            }, onClickRetry = {
                onEvent(HomeEvent.RetryTopPicks)
            }, onClickSeeAll = {
                onAction(HomeAction.OnGoToAllTopPicks)
            })
        }
    }
}
