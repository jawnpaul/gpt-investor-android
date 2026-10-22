package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.added_to_watchlist_error
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.failed_to_remove_from_watchlist
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickAction
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickEvent
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.AllTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.SavedTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.TopPickDetailScreen
import com.thejawnpaul.gptinvestor.removed_from_watchlist
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiToastResult
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.top_pick_save_failed
import com.thejawnpaul.gptinvestor.top_pick_saved_to_picks
import com.thejawnpaul.gptinvestor.view
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.topPickNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(
        route = Screen.TopPickDetailScreen.route,
        arguments = listOf(navArgument("topPickId") { NavType.StringType })
    ) {
        val viewModel = koinViewModel<TopPickViewModel>()
        val state = viewModel.topPickView.collectAsState()
        val scope = rememberCoroutineScope()
        val toastState = rememberGiToastState()
        val haptics = LocalHapticFeedback.current

        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    TopPickAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is TopPickAction.OnShare -> {
                        platformActions.shareText(action.url)
                    }

                    is TopPickAction.ShowToast -> {
                        scope.launch { toastState.show(message = action.message) }
                    }

                    TopPickAction.OnGoToLogin -> {
                        navController.navigateClearingBackStack(Screen.LoginScreen.route)
                    }

                    is TopPickAction.OpenCompanyAsk -> {
                        navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker, action.query))
                    }

                    TopPickAction.WatchlistAddFailed -> {
                        haptics.performHapticFeedback(GiHaptics.Reject)
                        scope.launch { toastState.show(message = getString(Res.string.added_to_watchlist_error)) }
                    }

                    is TopPickAction.WatchlistRemoved -> {
                        scope.launch {
                            toastState.show(message = getString(Res.string.removed_from_watchlist, action.ticker))
                        }
                    }

                    is TopPickAction.WatchlistRemoveFailed -> {
                        haptics.performHapticFeedback(GiHaptics.Reject)
                        scope.launch {
                            toastState.show(
                                message = getString(Res.string.failed_to_remove_from_watchlist, action.ticker)
                            )
                        }
                    }

                    TopPickAction.PickSaved -> {
                        scope.launch {
                            val result = toastState.show(
                                message = getString(Res.string.top_pick_saved_to_picks),
                                icon = GiIcons.check,
                                actionText = getString(Res.string.view)
                            )
                            if (result == GiToastResult.ActionPerformed) {
                                navController.navigate(Screen.SavedTopPicksScreen.route)
                            }
                        }
                    }

                    TopPickAction.PickSaveFailed -> {
                        haptics.performHapticFeedback(GiHaptics.Reject)
                        scope.launch {
                            val result = toastState.show(
                                message = getString(Res.string.top_pick_save_failed),
                                icon = GiIcons.warningCircle,
                                actionText = getString(Res.string.retry)
                            )
                            if (result == GiToastResult.ActionPerformed) {
                                viewModel.handleEvent(TopPickEvent.BookmarkTopPick)
                            }
                        }
                    }
                }
            }.launchIn(scope)
        }

        TopPickDetailScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent,
            onAction = viewModel::processAction,
            toastState = toastState
        )
    }

    composable(route = Screen.AllTopPicksScreen.route) {
        val viewModel = koinViewModel<TopPickViewModel>()
        val state = viewModel.allTopPicks.collectAsState()
        AllTopPicksScreen(
            modifier = Modifier,
            state = state.value,
            onGoBack = { navController.navigateUp() },
            onGoToDetail = { id -> navController.navigate(Screen.TopPickDetailScreen.createRoute(id)) }
        )
    }

    composable(route = Screen.SavedTopPicksScreen.route) {
        val viewModel = koinViewModel<TopPickViewModel>()
        val state = viewModel.savedTopPicks.collectAsState()
        SavedTopPicksScreen(
            modifier = Modifier,
            state = state.value,
            onGoBack = { navController.navigateUp() },
            onGoToDetail = { id -> navController.navigate(Screen.TopPickDetailScreen.createRoute(id)) },
            onSeeTodaysPicks = { navController.navigate(Screen.AllTopPicksScreen.route) }
        )
    }
}
