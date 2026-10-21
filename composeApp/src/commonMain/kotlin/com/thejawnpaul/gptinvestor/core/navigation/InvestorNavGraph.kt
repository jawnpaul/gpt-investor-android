package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.DigestDetailScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.investorNavGraph(navController: NavHostController) {
    composable(Screen.HomeTabScreen.route) {
        val homeViewModel = koinViewModel<HomeViewModel>()
        val state = homeViewModel.uiState.collectAsState()
        val haptics = LocalHapticFeedback.current
        val toastState = rememberGiToastState()
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
                        launch { toastState.show(message = action.message) }
                    }

                    is HomeAction.AddStockFinished -> {
                        haptics.performHapticFeedback(if (action.added) GiHaptics.Confirm else GiHaptics.Reject)
                    }

                    HomeAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }

                    HomeAction.NavigateToDiscover -> {
                        navController.navigateToTab(Screen.DiscoverTabScreen.route, Screen.HomeTabScreen.route)
                    }

                    HomeAction.NavigateToDigestDetail -> {
                        navController.navigate(Screen.DigestDetailScreen.route)
                    }

                    // Same as tapping the Profile tab, so the bottom bar's back stack stays consistent.
                    HomeAction.NavigateToProfile -> {
                        navController.navigateToTab(Screen.ProfileTabScreen.route, Screen.HomeTabScreen.route)
                    }
                }
            }.launchIn(this)
        }

        HomeScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = homeViewModel::handleEvent,
            toastState = toastState,
            composerModifier = askComposerTransition()
        )
    }

    composable(Screen.DigestDetailScreen.route) {
        val parentEntry = remember(navController) {
            navController.getBackStackEntry(Screen.HomeTabScreen.route)
        }
        val homeViewModel = koinViewModel<HomeViewModel>(viewModelStoreOwner = parentEntry)
        val state = homeViewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            homeViewModel.handleEvent(HomeEvent.DigestDetailViewed)
        }
        LaunchedEffect(Unit) {
            homeViewModel.actions.onEach { action ->
                if (action is HomeAction.OnGoToCompanyDetail) {
                    navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker))
                }
            }.launchIn(this)
        }

        DigestDetailScreen(
            digestView = state.value.dailyDigestView,
            onBack = { navController.popBackStack() },
            onStockClick = { homeViewModel.handleEvent(HomeEvent.ClickDigestStock(it)) },
            onUnlockPremium = {
                homeViewModel.handleEvent(HomeEvent.DigestDetailUnlockPremium)
            },
            onRetry = { homeViewModel.handleEvent(HomeEvent.RetryDailyDigest) }
        )
    }
}
