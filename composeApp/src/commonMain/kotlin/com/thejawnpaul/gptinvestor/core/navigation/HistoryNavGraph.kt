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
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryDetailScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.historyNavGraph(
    navController: NavHostController,
    platformActions: PlatformActions,
    platformContext: PlatformContext
) {
    composable(Screen.HistoryTabScreen.route) {
        val viewModel = koinViewModel<HistoryViewModel>()
        val state = viewModel.historyScreenViewState.collectAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    is HistoryScreenAction.OnGoToHistoryDetail -> {
                        navController.navigate(Screen.HistoryDetailScreen.createRoute(action.conversationId))
                    }

                    HistoryScreenAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is HistoryScreenAction.ShowToast -> {
                        platformActions.showMessage(action.message)
                    }
                }
            }.launchIn(scope)
        }

        HistoryScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent
        )
    }

    composable(
        route = Screen.HistoryDetailScreen.route,
        arguments = listOf(navArgument("conversationId") { NavType.StringType })
    ) {
        val viewModel = koinViewModel<HistoryViewModel>()
        val state = viewModel.conversation.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.historyDetailAction.onEach { action ->
                when (action) {
                    HistoryDetailAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is HistoryDetailAction.OnGoToWebView -> {
                        navController.navigate(Screen.WebViewScreen.createRoute(action.url))
                    }

                    is HistoryDetailAction.OnCopy -> {
                        platformActions.copyToClipboard("", action.text)
                        platformActions.showMessage("Copied")
                    }

                    is HistoryDetailAction.ShowToast -> {
                        platformActions.showMessage(action.message)
                    }

                    HistoryDetailAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }.launchIn(scope)
        }

        HistoryDetailScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleHistoryDetailEvent,
            onAction = viewModel::processHistoryDetailAction,
            onUpgradeFromRateLimit = {
                viewModel.launchPurchaseFlow(platformContext)
            }
        )
    }
}
