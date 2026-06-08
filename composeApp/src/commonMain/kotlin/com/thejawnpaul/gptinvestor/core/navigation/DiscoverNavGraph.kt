package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.discover.DiscoverViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoveryAction
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.discoverNavGraph(navController: NavHostController) {
    composable(
        route = Screen.DiscoverTabScreen.route,
        deepLinks = listOf(navDeepLink { uriPattern = Screen.DiscoverTabScreen.DEEP_LINK }),
        arguments = listOf(
            navArgument("sector") {
                nullable = true
                defaultValue = null
            }
        )
    ) {
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
                }
            }.launchIn(scope)
        }

        DiscoverScreen(
            modifier = Modifier,
            state = state.value,
            paging = viewModel.companiesPagingData,
            onEvent = viewModel::handleEvent
        )
    }
}
