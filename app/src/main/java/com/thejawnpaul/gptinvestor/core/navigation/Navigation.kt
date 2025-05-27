package com.thejawnpaul.gptinvestor.core.navigation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.WebViewScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDiscoveryAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryDetailScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsScreen
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickAction
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
                    val state = parentViewModel.selectedCompany.collectAsStateWithLifecycle()
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        parentViewModel.companyDetailAction.onEach { action ->
                            when (action) {
                                CompanyDetailAction.OnGoBack -> {
                                    navController.navigateUp()
                                }

                                is CompanyDetailAction.OnNavigateToWebView -> {
                                    navController.navigate(Screen.WebViewScreen.createRoute(action.url))
                                }
                            }
                        }.launchIn(scope)
                    }

                    CompanyDetailScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        ticker = ticker,
                        state = state.value,
                        onEvent = parentViewModel::handleCompanyDetailEvent,
                        onAction = parentViewModel::processCompanyDetailAction
                    )
                }

                composable(
                    route = Screen.ConversationScreen.route,
                    arguments = listOf(navArgument("chatInput") { NavType.StringType })
                ) { navBackStackEntry ->
                    val viewModel = hiltViewModel<ConversationViewModel>()
                    val state = viewModel.conversation.collectAsStateWithLifecycle()
                    val chatInput = navBackStackEntry.arguments?.getString("chatInput")
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
                            }
                        }.launchIn(scope)
                    }

                    ConversationScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        chatInput = chatInput,
                        state = state.value,
                        onEvent = viewModel::handleEvent,
                        onAction = viewModel::processAction
                    )
                }

                composable(route = Screen.WebViewScreen.route) { navBackStackEntry ->

                    val data = navBackStackEntry.arguments?.getString("url") ?: ""
                    WebViewScreen(url = data, onGoBack = {
                        navController.navigateUp()
                    })
                }

                composable(
                    route = Screen.HistoryDetailScreen.route,
                    arguments = listOf(navArgument("conversationId") { NavType.StringType })
                ) { navBackStackEntry ->
                    val viewModel = hiltViewModel<HistoryViewModel>()
                    val state = viewModel.conversation.collectAsStateWithLifecycle()
                    val id = navBackStackEntry.arguments?.getString("conversationId") ?: ""
                    val scope = rememberCoroutineScope()
                    LaunchedEffect(Unit) {
                        viewModel.historyDetailAction.onEach { action ->
                            when (action) {
                                HistoryDetailAction.OnGoBack -> {
                                    navController.navigateUp()
                                }

                                is HistoryDetailAction.OnGoToWebView -> {
                                    navController.navigate(Screen.WebViewScreen.createRoute(action.url))
                                }
                            }
                        }.launchIn(scope)
                    }

                    HistoryDetailScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        conversationId = id,
                        state = state.value,
                        onEvent = viewModel::handleHistoryDetailEvent,
                        onAction = viewModel::processHistoryDetailAction
                    )
                }

                composable(
                    route = Screen.TopPickDetailScreen.route,
                    arguments = listOf(navArgument("topPickId") { NavType.StringType })
                ) { navBackStackEntry ->
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    val state = viewModel.topPickView.collectAsStateWithLifecycle()
                    val id = navBackStackEntry.arguments?.getString("topPickId") ?: ""
                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        viewModel.actions.onEach { action ->
                            when (action) {
                                TopPickAction.OnGoBack -> {
                                    navController.navigateUp()
                                }

                                is TopPickAction.OnShare -> {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Top pick")
                                        putExtra(Intent.EXTRA_TEXT, action.url)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            sendIntent,
                                            "Share via"
                                        )
                                    )
                                }

                                is TopPickAction.ShowToast -> {
                                    Toast.makeText(context, action.message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }.launchIn(scope)
                    }

                    TopPickDetailScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        topPickId = id,
                        state = state.value,
                        onEvent = viewModel::handleEvent,
                        onAction = viewModel::processAction
                    )
                }

                composable(route = Screen.AllTopPicksScreen.route) {
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    val state = viewModel.allTopPicks.collectAsStateWithLifecycle()
                    AllTopPicksScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        state = state.value,
                        onGoBack = {
                            navController.navigateUp()
                        },
                        onGoToDetail = { id ->
                            navController.navigate(Screen.TopPickDetailScreen.createRoute(id))
                        }
                    )
                }

                composable(route = Screen.SavedTopPicksScreen.route) {
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    val state = viewModel.savedTopPicks.collectAsStateWithLifecycle()
                    SavedTopPicksScreen(
                        modifier = Modifier.padding(top = 20.dp),
                        state = state.value,
                        onGoBack = {
                            navController.navigateUp()
                        },
                        onGoToDetail = { id ->
                            navController.navigate(Screen.TopPickDetailScreen.createRoute(id))
                        }
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
