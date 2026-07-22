package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.DigestDetailScreen
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
                        navController.navigate(Screen.DigestDetailScreen.route)
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

    composable(Screen.DigestDetailScreen.route) {
        val parentEntry = remember(navController) {
            navController.getBackStackEntry(Screen.HomeTabScreen.route)
        }
        val homeViewModel = koinViewModel<HomeViewModel>(viewModelStoreOwner = parentEntry)
        val state = homeViewModel.uiState.collectAsState()

        DigestDetailScreen(
            digestView = state.value.dailyDigestView,
            onBack = { navController.popBackStack() },
            onUnlockPremium = { navController.navigate(Screen.ProfileTabScreen.route) }
        )
    }
}
