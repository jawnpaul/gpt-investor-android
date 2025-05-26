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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.TopPicks
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavHostController, viewModel: HomeViewModel, onMenuClick: () -> Unit) {
    // Home Screen

    val trendingStock = viewModel.trendingCompanies.collectAsStateWithLifecycle()
    val topPicks = viewModel.topPicks.collectAsStateWithLifecycle()
    val currentUser = viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedTheme = viewModel.theme.collectAsState(initial = "Dark")
    val homeState = viewModel.homeState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.actions.onEach { action ->
            when (action) {
                is HomeAction.OnSendClick -> {
                    navController.navigate(
                        Screen.ConversationScreen.createRoute(
                            chatInput = action.input ?: ""
                        )
                    )
                }
            }
        }.launchIn(scope)
    }

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
                IconButton(onClick = onMenuClick) {
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
                        viewModel.handleEvent(HomeEvent.ChangeTheme(it))
                    },
                    options = listOf("Light", "Dark", "System"),
                    selectedOption = selectedTheme.value ?: "Dark"
                )
            }

            // Trending
            TrendingStockList(
                modifier = Modifier.padding(vertical = 16.dp),
                state = trendingStock.value,
                onClick = {
                    navController.navigate(Screen.CompanyDetailScreen.createRoute(it))
                },
                onClickRetry = { viewModel.getTrendingCompanies() }
            )

            QuestionInput(
                modifier = Modifier,
                onSendClicked = {
                    viewModel.handleEvent(HomeEvent.SendClick)
                },
                hint = stringResource(R.string.ask_me_a_question),
                onTextChange = {
                    viewModel.handleEvent(HomeEvent.ChatInputChanged(it))
                },
                text = homeState.value.chatInput ?: ""
            )

            // Top Picks
            if (topPicks.value.topPicks.isNotEmpty()) {
                TopPicks(modifier = Modifier, state = topPicks.value, onClick = {
                    navController.navigate(Screen.TopPickDetailScreen.createRoute(it))
                }, onClickRetry = {
                    viewModel.getTopPicks()
                }, onClickSeeAll = {
                    navController.navigate(Screen.AllTopPicksScreen.route)
                })
            }
        }
    }
}
