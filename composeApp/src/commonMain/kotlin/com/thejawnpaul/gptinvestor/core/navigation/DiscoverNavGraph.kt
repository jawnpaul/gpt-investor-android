package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.discover.DiscoverViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoveryAction
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.discoverNavGraph(navController: NavHostController) {
    composable(route = Screen.DiscoverTabScreen.route) {
        val viewModel = koinViewModel<DiscoverViewModel>()
        val state = viewModel.discoveryScreenState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    is DiscoveryAction.OnNavigateToCompanyDetail -> {
                        navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker))
                    }

                    DiscoveryAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is DiscoveryAction.OnGoToPickDetail -> {
                        navController.navigate(
                            route = Screen.TopPickDetailScreen.createRoute(topPickId = action.id)
                        )
                    }

                    DiscoveryAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }

                    DiscoveryAction.OnGoToSearch -> {
                        navController.navigate(route = Screen.SearchScreen.route)
                    }

                    is DiscoveryAction.OnGoToTidbitDetail -> {
                        navController.navigate(route = Screen.TidbitDetailScreen.createRoute(tidbitId = action.id))
                    }

                    DiscoveryAction.OnGoToBrowse -> {
                        navController.navigate(Screen.BrowseStocksScreen.route)
                    }
                }
            }.launchIn(scope)
        }

        DiscoverScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent
        )
    }
}
