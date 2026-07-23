package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.added_to_watchlist
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.failed_to_remove_from_watchlist
import com.thejawnpaul.gptinvestor.removed_from_watchlist
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui.WatchlistScreen
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel.WatchlistAction
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel.WatchlistViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.watchlistNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(Screen.WatchlistTabScreen.route) {
        val viewModel = koinViewModel<WatchlistViewModel>()
        val state by viewModel.uiState.collectAsState()

        val addedTemplate = stringResource(Res.string.added_to_watchlist)
        val removedTemplate = stringResource(Res.string.removed_from_watchlist)
        val removeErrorTemplate = stringResource(Res.string.failed_to_remove_from_watchlist)

        LaunchedEffect(Unit) {
            viewModel.actions.collectLatest { action ->
                when (action) {
                    WatchlistAction.NavigateToSearch -> navController.navigate(Screen.SearchScreen.route)
                    WatchlistAction.NavigateToTopPicks -> navController.navigate(Screen.AllTopPicksScreen.route)
                    is WatchlistAction.NavigateToCompanyDetail -> navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker))
                    is WatchlistAction.ShowAddedMessage -> platformActions.showMessage(addedTemplate.format(action.ticker))
                    is WatchlistAction.ShowRemovedMessage -> platformActions.showMessage(removedTemplate.format(action.ticker))
                    is WatchlistAction.ShowRemoveErrorMessage -> platformActions.showMessage(removeErrorTemplate.format(action.ticker))
                }
            }
        }

        WatchlistScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}
