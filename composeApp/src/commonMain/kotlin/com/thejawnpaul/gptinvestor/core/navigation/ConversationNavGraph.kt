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
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.conversationNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(
        route = Screen.ConversationScreen.route,
        arguments = listOf(
            navArgument("chatInput") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("title") {
                type = NavType.StringType
                nullable = true
            }
        )
    ) {
        val viewModel = koinViewModel<ConversationViewModel>()
        val state = viewModel.conversation.collectAsState()
        val scope = rememberCoroutineScope()
        val toastState = rememberGiToastState()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    ConversationAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is ConversationAction.OnGoToWebView -> {
                        navController.navigate(Screen.WebViewScreen.createRoute(action.url))
                    }

                    is ConversationAction.OnCopy -> {
                        platformActions.copyToClipboard("", action.text)
                        scope.launch { toastState.show(message = "Copied") }
                    }

                    is ConversationAction.ShowToast -> {
                        scope.launch { toastState.show(message = action.message) }
                    }

                    ConversationAction.OnSignOutGuest -> {}
                    ConversationAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }

                    is ConversationAction.OnShare -> {
                        platformActions.shareText(action.text)
                    }

                    ConversationAction.OnGoToHistory -> {
                        navController.navigate(Screen.HistoryTabScreen.route) { launchSingleTop = true }
                    }

                    is ConversationAction.OnOpenCompany -> {
                        navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker))
                    }
                }
            }.launchIn(scope)
        }

        ConversationScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent,
            onAction = viewModel::processAction,
            toastState = toastState,
            onUpgradeFromRateLimit = {
                viewModel.handleUpgradeClick()
            },
            composerModifier = askComposerTransition()
        )
    }
}
