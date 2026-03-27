package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationViewModel
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginUiAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginViewModel
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpUiAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpViewModel
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.WebViewScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.discover.DiscoverViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoveryAction
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryDetailScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsAction
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsScreen
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsViewModel
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.SavedTidbitScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.TidbitDetailScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.TidbitScreen
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitAction
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailAction
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickAction
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.AllTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.SavedTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.TopPickDetailScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isUserSignedIn: Boolean = false,
    isGuestSignedIn: Boolean = false
) {
    val platformContext: PlatformContext = koinInject()
    val platformActions: PlatformActions = koinInject()
    Scaffold { innerPadding ->

        val startDestination: String =
            if (isUserSignedIn ||
                isGuestSignedIn
            ) {
                Screen.HomeTabScreen.route
            } else {
                Screen.DefaultAuthenticationScreen.route
            }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
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

                            is HomeAction.OnStartConversation -> {
                                navController.navigate(
                                    Screen.ConversationScreen.createRoute(
                                        chatInput = action.input ?: "",
                                        title = action.title
                                    )
                                )
                            }

                            HomeAction.OnGoToDiscover -> {
                                navController.navigate(Screen.DiscoverTabScreen.route)
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
                                    route = Screen.TidbitDetailScreen.createRoute(
                                        tidbitId = action.id
                                    )
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
                        }
                    }.launchIn(scope)
                }

                HomeScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onEvent = homeViewModel::handleEvent
                )
            }
            composable(
                route = Screen.DiscoverTabScreen.route,
                deepLinks = listOf(navDeepLink { uriPattern = Screen.DiscoverTabScreen.DEEP_LINK })
            ) {
                val viewModel = koinViewModel<DiscoverViewModel>()
                val state = viewModel.discoveryScreenState.collectAsState()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            is DiscoveryAction.OnNavigateToCompanyDetail -> {
                                navController.navigate(
                                    Screen.CompanyDetailScreen.createRoute(
                                        action.ticker
                                    )
                                )
                            }

                            DiscoveryAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is DiscoveryAction.OnGoToPickDetail -> {
                                navController.navigate(
                                    route = Screen.TopPickDetailScreen.createRoute(
                                        topPickId = action.id
                                    )
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
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    paging = viewModel.companiesPagingData,
                    onEvent = viewModel::handleEvent
                )
            }
            composable(Screen.HistoryTabScreen.route) {
                val viewModel = koinViewModel<HistoryViewModel>()
                val state = viewModel.historyScreenViewState.collectAsState()
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            is HistoryScreenAction.OnGoToHistoryDetail -> {
                                navController.navigate(
                                    Screen.HistoryDetailScreen.createRoute(action.conversationId)
                                )
                            }

                            HistoryScreenAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is HistoryScreenAction.ShowToast -> {
                                platformActions.showMessage(action.message)
                            }
                        }
                    }.launchIn(scope)
                }

                HistoryScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onEvent = viewModel::handleEvent
                )
            }

            composable(route = Screen.CompanyDetailScreen.route) { backStackEntry ->
                val parentViewModel = koinViewModel<CompanyViewModel>()
                val state = parentViewModel.selectedCompany.collectAsState()
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

                            is CompanyDetailAction.OnCopy -> {
                                platformActions.copyToClipboard("", action.text)
                                platformActions.showMessage("Copied")
                            }

                            CompanyDetailAction.OnGoToSignUp -> {
                                navController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }

                            is CompanyDetailAction.ShowToast -> {
                                platformActions.showMessage(action.message)
                            }
                        }
                    }.launchIn(scope)
                }

                CompanyDetailScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onEvent = parentViewModel::handleCompanyDetailEvent,
                    onAction = parentViewModel::processCompanyDetailAction
                )
            }

            composable(
                route = Screen.ConversationScreen.route,
                arguments = listOf(
                    navArgument("chatInput") {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument("title") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { navBackStackEntry ->
                val viewModel = koinViewModel<ConversationViewModel>()
                val state = viewModel.conversation.collectAsState()
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

                            is ConversationAction.OnCopy -> {
                                platformActions.copyToClipboard("", action.text)
                                platformActions.showMessage("Copied")
                            }

                            is ConversationAction.ShowToast -> {
                                platformActions.showMessage(action.message)
                            }

                            ConversationAction.OnSignOutGuest -> {}
                            ConversationAction.OnGoToSignUp -> {
                                navController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        }
                    }.launchIn(scope)
                }

                ConversationScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onEvent = viewModel::handleEvent,
                    onAction = viewModel::processAction,
                    onUpgradeFromRateLimit = {
                        if (isGuestSignedIn) {
                            navController.navigate(Screen.SignUpScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        } else {
                            viewModel.launchPurchaseFlow(platformContext)
                        }
                    }
                )
            }

            composable(
                route = Screen.WebViewScreen.route,
                arguments = listOf(navArgument("url") { type = NavType.StringType })
            ) { navBackStackEntry ->

                val data = navBackStackEntry.savedStateHandle.get<String>("url") ?: ""
                WebViewScreen(url = data, onGoBack = {
                    navController.navigateUp()
                })
            }

            composable(
                route = Screen.HistoryDetailScreen.route,
                arguments = listOf(navArgument("conversationId") { NavType.StringType })
            ) { navBackStackEntry ->
                val viewModel = koinViewModel<HistoryViewModel>()
                val state = viewModel.conversation.collectAsState()
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

                            is HistoryDetailAction.OnCopy -> {
                                platformActions.copyToClipboard("", action.text)
                                platformActions.showMessage("Copied")
                            }

                            is HistoryDetailAction.ShowToast -> {
                                platformActions.showMessage(action.message)
                            }

                            HistoryDetailAction.OnGoToSignUp -> {
                                navController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        }
                    }.launchIn(scope)
                }

                HistoryDetailScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onEvent = viewModel::handleHistoryDetailEvent,
                    onAction = viewModel::processHistoryDetailAction,
                    onUpgradeFromRateLimit = {
                        if (isGuestSignedIn) {
                            navController.navigate(Screen.SignUpScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        } else {
                            viewModel.launchPurchaseFlow(platformContext)
                        }
                    }
                )
            }

            composable(
                route = Screen.TopPickDetailScreen.route,
                arguments = listOf(navArgument("topPickId") { NavType.StringType })
            ) { navBackStackEntry ->
                val viewModel = koinViewModel<TopPickViewModel>()
                val state = viewModel.topPickView.collectAsState()
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            TopPickAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is TopPickAction.OnShare -> {
                                platformActions.shareText(action.url)
                            }

                            is TopPickAction.ShowToast -> {
                                platformActions.showMessage(action.message)
                            }

                            TopPickAction.OnGoToSignUp -> {
                                navController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        }
                    }.launchIn(scope)
                }

                TopPickDetailScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onEvent = viewModel::handleEvent,
                    onAction = viewModel::processAction
                )
            }

            composable(route = Screen.AllTopPicksScreen.route) {
                val viewModel = koinViewModel<TopPickViewModel>()
                val state = viewModel.allTopPicks.collectAsState()
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
                val viewModel = koinViewModel<TopPickViewModel>()
                val state = viewModel.savedTopPicks.collectAsState()
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
                val viewModel = koinViewModel<SettingsViewModel>()
                val state = viewModel.uiState.collectAsState()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            SettingsAction.OnGoBack -> {
                                navController.navigateUp()
                            }
                        }
                    }.launchIn(scope)
                }

                SettingsScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onEvent = viewModel::handleEvent,
                    onAction = viewModel::processAction
                )
            }

            composable(
                route = Screen.TidbitDetailScreen.route,
                arguments = listOf(navArgument("tidbitId") { NavType.StringType })
            ) { navBackStackEntry ->
                val viewModel = koinViewModel<TidbitViewModel>()
                val state = viewModel.tidbitDetailState.collectAsState()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.tidbitDetailActions.onEach { action ->
                        when (action) {
                            TidbitDetailAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is TidbitDetailAction.OnOpenSource -> {
                                platformActions.openUrl(action.url)
                            }

                            is TidbitDetailAction.OnShare -> {
                                platformActions.shareText(action.shareText)
                            }

                            TidbitDetailAction.OnGoToSignUp -> {
                                navController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        }
                    }.launchIn(scope)
                }

                TidbitDetailScreen(
                    modifier = Modifier,
                    onEvent = viewModel::handleDetailEvent,
                    state = state.value
                )
            }

            composable(route = Screen.TidbitScreen.route) {
                val viewModel = koinViewModel<TidbitViewModel>()
                val state = viewModel.tidbitMainScreenState.collectAsState()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            TidbitAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is TidbitAction.OnShare -> {
                                platformActions.shareText(action.shareText)
                            }

                            is TidbitAction.OnGoToTidbitDetail -> {
                                navController.navigate(
                                    Screen.TidbitDetailScreen.createRoute(
                                        tidbitId = action.tidbitId
                                    )
                                )
                            }
                        }
                    }.launchIn(scope)
                }

                TidbitScreen(
                    modifier = Modifier,
                    state = state.value,
                    onEvent = viewModel::handleMainScreenEvent,
                    tidbitsPagingData = viewModel.tidbitsPagingData
                )
            }

            composable(route = Screen.SavedTidbitScreen.route) {
                val viewModel = koinViewModel<TidbitViewModel>()
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            TidbitAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is TidbitAction.OnGoToTidbitDetail -> {
                                navController.navigate(
                                    route = Screen.TidbitDetailScreen.createRoute(
                                        tidbitId = action.tidbitId
                                    )
                                )
                            }

                            is TidbitAction.OnShare -> {
                                platformActions.shareText(action.shareText)
                            }
                        }
                    }.launchIn(scope)
                }

                SavedTidbitScreen(
                    modifier = Modifier,
                    tidbitsPagingData = viewModel.tidbitsPagingData,
                    onEvent = viewModel::handleMainScreenEvent
                )
            }

            composable(route = Screen.DefaultAuthenticationScreen.route) {
                val viewModel = koinViewModel<DefaultAuthenticationViewModel>()
                val state = viewModel.loading.collectAsState()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            DefaultAuthenticationAction.OnGoToHome -> {
                                navController.navigate(Screen.HomeTabScreen.route) {
                                    popUpTo(Screen.DefaultAuthenticationScreen.route) {
                                        inclusive = true
                                    }
                                }
                            }

                            DefaultAuthenticationAction.OnGoToSignUp -> {
                                navController.navigate(Screen.SignUpScreen.route) {
                                    if (isGuestSignedIn) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            }

                            is DefaultAuthenticationAction.ShowToast -> {
                                platformActions.showMessage(action.message)
                            }
                        }
                    }.launchIn(scope)
                }

                DefaultAuthenticationScreen(
                    modifier = Modifier,
                    onEvent = viewModel::handleEvent,
                    loading = state.value
                )
            }

            composable(route = Screen.LoginScreen.route) {
                val viewModel = koinViewModel<LoginViewModel>()
                val state = viewModel.loginUiState.collectAsState()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            LoginUiAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            LoginUiAction.OnGoToSignUp -> {
                                navController.navigate(Screen.SignUpScreen.route) {
                                    if (isGuestSignedIn) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            }

                            is LoginUiAction.OnShowToast -> {
                                platformActions.showMessage(action.message)
                            }

                            LoginUiAction.OnGoToHome -> {
                                navController.navigate(route = Screen.HomeTabScreen.route) {
                                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
                                }
                            }
                        }
                    }.launchIn(scope)
                }

                LoginScreen(state = state.value, onEvent = viewModel::handleEvent)
            }

            composable(
                route = Screen.SignUpScreen.route
            ) {
                val viewModel = koinViewModel<SignUpViewModel>()
                val state = viewModel.signUpUiState.collectAsState()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            SignUpUiAction.OnGoBack -> {
                                navController.popBackStack()
                            }

                            SignUpUiAction.OnGoToHome -> {
                                navController.navigate(route = Screen.HomeTabScreen.route) {
                                    popUpTo(Screen.SignUpScreen.route) {
                                        inclusive = true
                                    }
                                }
                            }

                            SignUpUiAction.OnGoToLogin -> {
                                navController.navigate(Screen.LoginScreen.route)
                            }

                            is SignUpUiAction.OnShowToast -> {
                                platformActions.showMessage(action.message)
                            }
                        }
                    }.launchIn(scope)
                }
                SignUpScreen(
                    modifier = Modifier,
                    state = state.value,
                    onEvent = viewModel::handleEvent
                )
            }
        }
    }

    LaunchedEffect(isUserSignedIn, isGuestSignedIn) {
        if (isUserSignedIn || isGuestSignedIn) {
            if (navController.currentDestination?.route == Screen.DefaultAuthenticationScreen.route) {
                navController.navigate(Screen.HomeTabScreen.route) {
                    popUpTo(Screen.DefaultAuthenticationScreen.route) { inclusive = true }
                }
            }
        } else {
            navController.navigate(Screen.DefaultAuthenticationScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
