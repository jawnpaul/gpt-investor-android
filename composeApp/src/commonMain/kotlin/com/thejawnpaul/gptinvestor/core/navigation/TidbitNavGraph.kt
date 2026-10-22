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
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.SavedTidbitScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.TidbitDetailScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.TidbitScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitAction
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailAction
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailEvent
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitViewModel
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiToastResult
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.tidbit_remove_failed
import com.thejawnpaul.gptinvestor.tidbit_save_failed
import com.thejawnpaul.gptinvestor.tidbit_saved_to_library
import com.thejawnpaul.gptinvestor.view
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.tidbitNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(
        route = Screen.TidbitDetailScreen.route,
        arguments = listOf(navArgument("tidbitId") { NavType.StringType })
    ) {
        val viewModel = koinViewModel<TidbitViewModel>()
        val state = viewModel.tidbitDetailState.collectAsState()
        val scope = rememberCoroutineScope()
        val toastState = rememberGiToastState()
        val haptics = LocalHapticFeedback.current
        LaunchedEffect(Unit) {
            viewModel.tidbitDetailActions.onEach { action ->
                when (action) {
                    TidbitDetailAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is TidbitDetailAction.OnOpenSource -> {
                        platformActions.openUrl(action.url)
                    }

                    is TidbitDetailAction.OnShare -> {
                        platformActions.shareText(action.shareText)
                    }

                    TidbitDetailAction.OnGoToLogin -> {
                        navController.navigateClearingBackStack(Screen.LoginScreen.route)
                    }

                    TidbitDetailAction.Saved -> {
                        scope.launch {
                            val result = toastState.show(
                                message = getString(Res.string.tidbit_saved_to_library),
                                icon = GiIcons.check,
                                actionText = getString(Res.string.view)
                            )
                            if (result == GiToastResult.ActionPerformed) {
                                navController.navigate(Screen.SavedTidbitScreen.route)
                            }
                        }
                    }

                    is TidbitDetailAction.SaveFailed -> {
                        haptics.performHapticFeedback(GiHaptics.Reject)
                        scope.launch {
                            val result = toastState.show(
                                message = getString(
                                    if (action.newValue) {
                                        Res.string.tidbit_save_failed
                                    } else {
                                        Res.string.tidbit_remove_failed
                                    }
                                ),
                                icon = GiIcons.warningCircle,
                                actionText = getString(Res.string.retry)
                            )
                            if (result == GiToastResult.ActionPerformed) {
                                viewModel.handleDetailEvent(
                                    TidbitDetailEvent.OnClickBookmark(id = action.id, newValue = action.newValue)
                                )
                            }
                        }
                    }
                }
            }.launchIn(scope)
        }

        TidbitDetailScreen(
            modifier = Modifier,
            onEvent = viewModel::handleDetailEvent,
            state = state.value,
            toastState = toastState
        )
    }

    composable(route = Screen.TidbitScreen.route) {
        val viewModel = koinViewModel<TidbitViewModel>()
        val state = viewModel.tidbitMainScreenState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    TidbitAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is TidbitAction.OnShare -> {
                        platformActions.shareText(action.shareText)
                    }

                    is TidbitAction.OnGoToTidbitDetail -> {
                        navController.navigate(
                            Screen.TidbitDetailScreen.createRoute(tidbitId = action.tidbitId)
                        )
                    }
                }
            }.launchIn(scope)
        }

        TidbitScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleMainScreenEvent,
            tidbitsPagingData = viewModel.tidbitsPagingData
        )
    }

    composable(route = Screen.SavedTidbitScreen.route) {
        val viewModel = koinViewModel<TidbitViewModel>()
        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) { viewModel.showSavedOnly() }

        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    TidbitAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is TidbitAction.OnGoToTidbitDetail -> {
                        navController.navigate(
                            route = Screen.TidbitDetailScreen.createRoute(tidbitId = action.tidbitId)
                        )
                    }

                    is TidbitAction.OnShare -> {
                        platformActions.shareText(action.shareText)
                    }
                }
            }.launchIn(scope)
        }

        SavedTidbitScreen(
            modifier = Modifier,
            tidbitsPagingData = viewModel.tidbitsPagingData,
            onEvent = viewModel::handleMainScreenEvent
        )
    }
}
