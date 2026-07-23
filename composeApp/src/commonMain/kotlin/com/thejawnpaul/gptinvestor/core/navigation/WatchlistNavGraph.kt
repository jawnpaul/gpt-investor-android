package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui.WatchlistScreen
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel.WatchlistAction
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel.WatchlistViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.watchlistNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(Screen.WatchlistTabScreen.route) {
        val viewModel = koinViewModel<WatchlistViewModel>()
        val state by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.actions.collectLatest { action ->
                when (action) {
                    WatchlistAction.NavigateToSearch -> {
                        navController.navigate(Screen.SearchScreen.route)
                    }
                    WatchlistAction.NavigateToTopPicks -> {
                        navController.navigate(Screen.AllTopPicksScreen.route)
                    }
                    is WatchlistAction.ShowMessage -> {
                        platformActions.showMessage(action.message)
                    }
                }
            }
        }

        WatchlistScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}
