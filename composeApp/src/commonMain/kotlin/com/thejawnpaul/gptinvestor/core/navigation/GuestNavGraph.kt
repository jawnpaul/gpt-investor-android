package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.added_to_watchlist
import com.thejawnpaul.gptinvestor.added_to_watchlist_error
import com.thejawnpaul.gptinvestor.added_to_watchlist_success
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.failed_to_remove_from_watchlist
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.discover.DiscoverViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoveryAction
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryDetailScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import com.thejawnpaul.gptinvestor.features.profile.presentation.ui.ProfileScreen
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileViewModel
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchAction
import com.thejawnpaul.gptinvestor.features.search.presentation.ui.SearchScreen
import com.thejawnpaul.gptinvestor.features.search.presentation.viewmodel.SearchViewModel
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
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui.WatchlistScreen
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel.WatchlistAction
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel.WatchlistViewModel
import com.thejawnpaul.gptinvestor.removed_from_watchlist
import com.thejawnpaul.gptinvestor.share_brief_text
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.guestNavGraph(
    navController: NavHostController,
    platformActions: PlatformActions,
    platformContext: PlatformContext
) {
    composable(GuestScreen.GuestHomeTab.route) {
        val viewModel = koinViewModel<HomeViewModel>()
        val state = viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    is HomeAction.OnGoToCompanyDetail -> {
                        navController.navigate(GuestScreen.GuestCompanyDetail.createRoute(action.ticker))
                    }

                    is HomeAction.OnStartConversation -> {
                        navController.navigate(
                            GuestScreen.GuestConversation.createRoute(
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
                        navController.navigate(GuestScreen.GuestDiscoverTab.route)
                    }

                    HomeAction.NavigateToProfile -> {
                        navController.navigate(GuestScreen.GuestProfileTab.route)
                    }

                    HomeAction.NavigateToDigestDetail -> {
                    }
                }
            }.launchIn(scope)
        }

        HomeScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent
        )
    }

    composable(
        route = GuestScreen.GuestDiscoverTab.route,
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
                        navController.navigate(GuestScreen.GuestCompanyDetail.createRoute(action.ticker))
                    }

                    DiscoveryAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is DiscoveryAction.OnGoToPickDetail -> {
                        navController.navigate(GuestScreen.GuestTopPickDetail.createRoute(action.id))
                    }

                    DiscoveryAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }

                    DiscoveryAction.OnGoToSearch -> {
                    }

                    is DiscoveryAction.OnGoToTidbitDetail -> {
                        navController.navigate(route = GuestScreen.GuestTidbitDetail.createRoute(action.id))
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

    composable(GuestScreen.GuestHistoryTab.route) {
        val viewModel = koinViewModel<HistoryViewModel>()
        val state = viewModel.historyScreenViewState.collectAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    is HistoryScreenAction.OnGoToHistoryDetail -> {
                        navController.navigate(GuestScreen.GuestHistoryDetail.createRoute(action.conversationId))
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
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent
        )
    }

    composable(
        route = GuestScreen.GuestConversation.route,
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
    ) {
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
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent,
            onAction = viewModel::processAction,
            onUpgradeFromRateLimit = {}
        )
    }

    composable(
        route = GuestScreen.GuestHistoryDetail.route,
        arguments = listOf(navArgument("conversationId") { NavType.StringType })
    ) {
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
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleHistoryDetailEvent,
            onAction = viewModel::processHistoryDetailAction,
            onUpgradeFromRateLimit = {}
        )
    }

    composable(route = GuestScreen.GuestCompanyDetail.route) {
        val viewModel = koinViewModel<CompanyViewModel>()
        val state = viewModel.selectedCompany.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.companyDetailAction.collect { action ->
                when (action) {
                    CompanyDetailAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is CompanyDetailAction.OnNavigateToWebView -> {
                        platformActions.openUrl(action.url)
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

                    is CompanyDetailAction.OnShare -> {
                        val message = getString(Res.string.share_brief_text, action.name, action.ticker, action.id)
                        platformActions.shareText(message)
                    }

                    CompanyDetailAction.WatchlistAdded -> {
                        platformActions.showMessage(getString(Res.string.added_to_watchlist_success))
                    }

                    CompanyDetailAction.WatchlistAddFailed -> {
                        platformActions.showMessage(getString(Res.string.added_to_watchlist_error))
                    }
                }
            }
        }

        CompanyDetailScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleCompanyDetailEvent,
            onAction = viewModel::processCompanyDetailAction
        )
    }

    composable(
        route = GuestScreen.GuestTopPickDetail.route,
        arguments = listOf(navArgument("topPickId") { NavType.StringType })
    ) {
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
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent,
            onAction = viewModel::processAction
        )
    }

    composable(route = GuestScreen.GuestAllTopPicks.route) {
        val viewModel = koinViewModel<TopPickViewModel>()
        val state = viewModel.allTopPicks.collectAsState()
        AllTopPicksScreen(
            modifier = Modifier,
            state = state.value,
            onGoBack = { navController.navigateUp() },
            onGoToDetail = { id -> navController.navigate(GuestScreen.GuestTopPickDetail.createRoute(id)) }
        )
    }

    composable(route = GuestScreen.GuestSavedTopPicks.route) {
        val viewModel = koinViewModel<TopPickViewModel>()
        val state = viewModel.savedTopPicks.collectAsState()
        SavedTopPicksScreen(
            modifier = Modifier,
            state = state.value,
            onGoBack = { navController.navigateUp() },
            onGoToDetail = { id -> navController.navigate(GuestScreen.GuestTopPickDetail.createRoute(id)) }
        )
    }

    composable(
        route = GuestScreen.GuestTidbitDetail.route,
        arguments = listOf(navArgument("tidbitId") { NavType.StringType })
    ) {
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

    composable(route = GuestScreen.GuestTidbitScreen.route) {
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
                            GuestScreen.GuestTidbitDetail.createRoute(tidbitId = action.tidbitId)
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

    composable(route = GuestScreen.GuestSavedTidbitScreen.route) {
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
                            route = GuestScreen.GuestTidbitDetail.createRoute(tidbitId = action.tidbitId)
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

    composable(route = GuestScreen.GuestWatchlistTab.route) {
        val viewModel = koinViewModel<WatchlistViewModel>()
        val state = viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.actions.collect { action ->
                when (action) {
                    WatchlistAction.NavigateToSearch -> navController.navigate(GuestScreen.GuestSearch.route)
                    WatchlistAction.NavigateToTopPicks -> navController.navigate(GuestScreen.GuestAllTopPicks.route)
                    is WatchlistAction.NavigateToCompanyDetail -> navController.navigate(
                        GuestScreen.GuestCompanyDetail.createRoute(action.ticker)
                    )
                    is WatchlistAction.ShowAddedMessage -> platformActions.showMessage(
                        getString(Res.string.added_to_watchlist, action.ticker)
                    )
                    is WatchlistAction.ShowRemovedMessage -> platformActions.showMessage(
                        getString(Res.string.removed_from_watchlist, action.ticker)
                    )
                    is WatchlistAction.ShowRemoveErrorMessage -> platformActions.showMessage(
                        getString(Res.string.failed_to_remove_from_watchlist, action.ticker)
                    )
                }
            }
        }
        WatchlistScreen(
            state = state.value,
            onEvent = viewModel::onEvent
        )
    }

    composable(route = GuestScreen.GuestProfileTab.route) {
        val viewModel: ProfileViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        ProfileScreen(
            state = state,
            onEvent = viewModel::handleEvent
        )
    }

    composable(route = GuestScreen.GuestSearch.route) {
        val viewModel = koinViewModel<SearchViewModel>()
        val state = viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    SearchAction.OnGoBack -> navController.popBackStack()
                    is SearchAction.OnNavigateToCompany ->
                        navController.navigate(GuestScreen.GuestCompanyDetail.createRoute(action.ticker))
                    is SearchAction.OnNavigateToConversation ->
                        navController.navigate(GuestScreen.GuestConversation.createRoute(chatInput = action.query))
                    is SearchAction.OnNavigateToSector ->
                        navController.navigate(GuestScreen.GuestDiscoverTab.createRoute(action.sectorKey))
                }
            }.launchIn(scope)
        }
        SearchScreen(state = state.value, onEvent = viewModel::handleEvent)
    }
}
