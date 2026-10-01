package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.BrowseStocksScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.BrowseStocksAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.BrowseStocksViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.browseStocksNavGraph(navController: NavHostController) {
    composable(
        route = Screen.BrowseStocksScreen.route,
        deepLinks = listOf(navDeepLink { uriPattern = Screen.BrowseStocksScreen.DEEP_LINK }),
        arguments = listOf(
            navArgument("sector") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) {
        val viewModel = koinViewModel<BrowseStocksViewModel>()
        val state = viewModel.state.collectAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    is BrowseStocksAction.NavigateToCompanyDetail ->
                        navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker))
                    BrowseStocksAction.GoBack ->
                        navController.popBackStack()
                }
            }.launchIn(scope)
        }

        BrowseStocksScreen(
            state = state.value,
            companiesPaging = viewModel.companiesPaging,
            onEvent = viewModel::handleEvent
        )
    }
}
