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
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.conversationNavGraph(
    navController: NavHostController,
    platformActions: PlatformActions,
    platformContext: PlatformContext,
    isGuestSignedIn: Boolean
) {
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
                        platformActions.showMessage("Copied")
                    }

                    is ConversationAction.ShowToast -> {
                        platformActions.showMessage(action.message)
                    }

                    ConversationAction.OnSignOutGuest -> {}
                    ConversationAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }.launchIn(scope)
        }

        ConversationScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent,
            onAction = viewModel::processAction,
            onUpgradeFromRateLimit = {
                if (isGuestSignedIn) {
                    navController.navigate(Screen.SignUpScreen.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                } else {
                    viewModel.launchPurchaseFlow(platformContext)
                }
            }
        )
    }
}
