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
                    HomeAction.OnGoToAllTopPicks -> {
                        navController.navigate(Screen.AllTopPicksScreen.route)
                    }

                    is HomeAction.OnGoToCompanyDetail -> {
                        navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker))
                    }

                    is HomeAction.OnGoToTopPickDetail -> {
                        navController.navigate(Screen.TopPickDetailScreen.createRoute(action.id))
                    }

                    is HomeAction.OnStartConversation -> {
                        navController.navigate(
                            Screen.ConversationScreen.createRoute(
                                chatInput = action.input ?: "",
                                title = action.title
                            )
                        )
                    }

                    HomeAction.OnGoToDiscover -> {
                        navController.navigate(Screen.DiscoverTabScreen.createRoute())
                    }

                    HomeAction.OnGoToHistory -> {
                        navController.navigate(Screen.HistoryTabScreen.route)
                    }

                    HomeAction.OnGoToSavedPicks -> {
                        navController.navigate(Screen.SavedTopPicksScreen.route)
                    }

                    HomeAction.OnGoToSettings -> {
                        navController.navigate(Screen.SettingsScreen.route)
                    }

                    HomeAction.OnGoToAllTidbits -> {
                        navController.navigate(route = Screen.TidbitScreen.route)
                    }

                    is HomeAction.OnGoToTidbitDetail -> {
                        navController.navigate(
                            route = Screen.TidbitDetailScreen.createRoute(tidbitId = action.id)
                        )
                    }

                    HomeAction.OnGoToSavedTidbits -> {
                        navController.navigate(route = Screen.SavedTidbitScreen.route)
                    }

                    is HomeAction.ShowToast -> {
                        platformActions.showMessage(action.message)
                    }

                    HomeAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }

                    HomeAction.NavigateToSearch -> {
                        navController.navigate(Screen.SearchScreen.route)
                    }

                    HomeAction.NavigateToAllTrending -> {
                        navController.navigate(Screen.AllTrendingScreen.route)
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
