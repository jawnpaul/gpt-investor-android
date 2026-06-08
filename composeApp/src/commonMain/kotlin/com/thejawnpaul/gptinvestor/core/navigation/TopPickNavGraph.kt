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
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickAction
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.AllTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.SavedTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.TopPickDetailScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.topPickNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(
        route = Screen.TopPickDetailScreen.route,
        arguments = listOf(navArgument("topPickId") { NavType.StringType })
    ) {
        val viewModel = koinViewModel<TopPickViewModel>()
        val state = viewModel.topPickView.collectAsState()
        val scope = rememberCoroutineScope()

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
                        platformActions.showMessage(action.message)
                    }

                    TopPickAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }.launchIn(scope)
        }

        TopPickDetailScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent,
            onAction = viewModel::processAction
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
            onGoToDetail = { id -> navController.navigate(Screen.TopPickDetailScreen.createRoute(id)) }
        )
    }
}
