package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchAction
import com.thejawnpaul.gptinvestor.features.search.presentation.ui.SearchScreen
import com.thejawnpaul.gptinvestor.features.search.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.searchNavGraph(navController: NavHostController) {
    composable(route = Screen.SearchScreen.route) {
        val viewModel = koinViewModel<SearchViewModel>()
        val state = viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    SearchAction.OnGoBack -> navController.popBackStack()
                    is SearchAction.OnNavigateToCompany ->
                        navController.navigate(Screen.CompanyDetailScreen.createRoute(action.ticker))
                    is SearchAction.OnNavigateToConversation ->
                        navController.navigate(Screen.ConversationScreen.createRoute(chatInput = action.query))
                    is SearchAction.OnNavigateToSector ->
                        navController.navigate(Screen.DiscoverTabScreen.createRoute(action.sectorKey))
                }
            }.launchIn(scope)
        }
        SearchScreen(state = state.value, onEvent = viewModel::handleEvent)
    }
}
