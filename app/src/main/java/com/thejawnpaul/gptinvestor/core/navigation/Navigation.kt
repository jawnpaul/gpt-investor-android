package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.features.authentication.presentation.AuthenticationScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.AuthenticationViewModel
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.WebViewScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDiscoveryAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryDetailScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsScreen
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.AllTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.SavedTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.TopPickDetailScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun SetUpNavGraph(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavDrawerContent(
                navController = navController,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = { BottomNavBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.HomeTabScreen.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.HomeTabScreen.route) {
                    val homeViewModel = hiltViewModel<HomeViewModel>()
                    val state = homeViewModel.uiState.collectAsStateWithLifecycle()
                    val scope = rememberCoroutineScope()
                    LaunchedEffect(Unit) {
                        homeViewModel.actions.onEach { action ->
                            when (action) {
                                HomeAction.OnGoToAllTopPicks -> {
                                    navController.navigate(Screen.AllTopPicksScreen.route)
                                }

                                is HomeAction.OnGoToCompanyDetail -> {
                                    navController.navigate(
                                        Screen.CompanyDetailScreen.createRoute(
                                            action.ticker
                                        )
                                    )
                                }

                                is HomeAction.OnGoToTopPickDetail -> {
                                    navController.navigate(
                                        Screen.TopPickDetailScreen.createRoute(
                                            action.id
                                        )
                                    )
                                }

                                HomeAction.OnMenuClick -> {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }

                                is HomeAction.OnStartConversation -> {
                                    navController.navigate(
                                        Screen.ConversationScreen.createRoute(
                                            chatInput = action.input ?: ""
                                        )
                                    )
                                }
                            }
                        }.launchIn(scope)
                    }

                    HomeScreen(
                        modifier = Modifier,
                        state = state.value,
                        onEvent = homeViewModel::handleEvent,
                        onAction = homeViewModel::processAction
                    )
                }
                composable(Screen.DiscoverTabScreen.route) {
                    val companyViewModel = hiltViewModel<CompanyViewModel>()
                    val state = companyViewModel.companyDiscoveryState.collectAsStateWithLifecycle()
                    val scope = rememberCoroutineScope()
                    LaunchedEffect(Unit) {
                        companyViewModel.companyDiscoveryAction.onEach { action ->
                            when (action) {
                                is CompanyDiscoveryAction.OnNavigateToCompanyDetail -> {
                                    navController.navigate(
                                        Screen.CompanyDetailScreen.createRoute(
                                            action.ticker
                                        )
                                    )
                                }
                            }
                        }.launchIn(scope)
                    }

                    DiscoverScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        state = state.value,
                        onEvent = companyViewModel::handleCompanyDiscoveryEvent,
                        onAction = companyViewModel::processCompanyDiscoveryAction
                    )
                }
                composable(Screen.HistoryTabScreen.route) {
                    val viewModel = hiltViewModel<HistoryViewModel>()
                    val state = viewModel.historyScreenViewState.collectAsStateWithLifecycle()
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        viewModel.actions.onEach { action ->
                            when (action) {
                                is HistoryScreenAction.OnGoToHistoryDetail -> {
                                    navController.navigate(
                                        Screen.HistoryDetailScreen.createRoute(action.conversationId)
                                    )
                                }
                            }
                        }.launchIn(scope)
                    }

                    HistoryScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        state = state.value,
                        onEvent = viewModel::handleEvent,
                        onAction = viewModel::processAction
                    )
                }

                composable(route = Screen.CompanyDetailScreen.route) { backStackEntry ->
                    val parentViewModel = hiltViewModel<CompanyViewModel>()
                    val ticker = backStackEntry.arguments?.getString("ticker") ?: ""
                    CompanyDetailScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        navController = navController,
                        viewModel = parentViewModel,
                        ticker = ticker
                    )
                }

                composable(
                    route = Screen.ConversationScreen.route,
                    arguments = listOf(navArgument("chatInput") { NavType.StringType })
                ) { navBackStackEntry ->
                    val viewModel = hiltViewModel<ConversationViewModel>()
                    val chatInput = navBackStackEntry.arguments?.getString("chatInput") ?: ""

                    ConversationScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        viewModel = viewModel,
                        navController = navController,
                        chatInput = chatInput
                    )
                }

                composable(route = Screen.WebViewScreen.route) { navBackStackEntry ->

                    val data = navBackStackEntry.arguments?.getString("url") ?: ""
                    WebViewScreen(navController = navController, url = data)
                }

                composable(
                    route = Screen.HistoryDetailScreen.route,
                    arguments = listOf(navArgument("conversationId") { NavType.StringType })
                ) { navBackStackEntry ->
                    val viewModel = hiltViewModel<HistoryViewModel>()
                    val id = navBackStackEntry.arguments?.getString("conversationId") ?: ""

                    HistoryDetailScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        navController = navController,
                        conversationId = id,
                        viewModel = viewModel
                    )
                }

                composable(
                    route = Screen.TopPickDetailScreen.route,
                    arguments = listOf(navArgument("topPickId") { NavType.StringType })
                ) { navBackStackEntry ->
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    val id = navBackStackEntry.arguments?.getString("topPickId") ?: ""
                    TopPickDetailScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        navController = navController,
                        topPickId = id,
                        viewModel = viewModel
                    )
                }

                composable(route = Screen.AllTopPicksScreen.route) {
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    AllTopPicksScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable(route = Screen.AuthenticationScreen.route) {
                    val viewModel = hiltViewModel<AuthenticationViewModel>()
                    AuthenticationScreen(
                        viewModel = viewModel,
                        onSignInSuccess = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(route = Screen.SavedTopPicksScreen.route) {
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    SavedTopPicksScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                composable(route = Screen.SettingsScreen.route) {
                    val viewModel = hiltViewModel<SettingsViewModel>()
                    SettingsScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
