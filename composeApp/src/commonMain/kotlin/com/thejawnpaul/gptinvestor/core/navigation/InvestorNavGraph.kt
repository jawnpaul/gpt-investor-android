package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.investorNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(Screen.HomeTabScreen.route) {
        val homeViewModel = koinViewModel<HomeViewModel>()
        val state = homeViewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            homeViewModel.actions.onEach { action ->
                when (action) {
                    is HomeAction.OnGoToCompanyDetail -> {
                        navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker))
                    }

                    is HomeAction.OnStartConversation -> {
                        navController.navigate(
                            Screen.ConversationScreen.createRoute(
                                chatInput = action.input ?: "",
                                title = action.title
                            )
                        )
                    }

                    is HomeAction.ShowToast -> {
                        platformActions.showMessage(action.message)
                    }

                    HomeAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }

                    HomeAction.NavigateToAllTrending -> {
                        navController.navigate(Screen.AllTrendingScreen.route)
                    }

                    HomeAction.NavigateToDiscover -> {
                        navController.navigate(Screen.DiscoverTabScreen.route)
                    }

                    HomeAction.NavigateToProfile -> {
                        navController.navigate(Screen.ProfileTabScreen.route)
                    }

                    HomeAction.NavigateToDigestDetail -> {
                    }
                }
            }.launchIn(scope)
        }

        HomeScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = homeViewModel::handleEvent
        )
    }
}
