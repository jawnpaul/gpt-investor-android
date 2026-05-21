package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.SavedTidbitScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.TidbitDetailScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.TidbitScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitAction
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailAction
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.tidbitNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(
        route = Screen.TidbitDetailScreen.route,
        arguments = listOf(navArgument("tidbitId") { NavType.StringType })
    ) {
        val viewModel = koinViewModel<TidbitViewModel>()
        val state = viewModel.tidbitDetailState.collectAsState()
        val scope = rememberCoroutineScope()
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

                    TidbitDetailAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }.launchIn(scope)
        }

        TidbitDetailScreen(
            modifier = Modifier,
            onEvent = viewModel::handleDetailEvent,
            state = state.value
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
