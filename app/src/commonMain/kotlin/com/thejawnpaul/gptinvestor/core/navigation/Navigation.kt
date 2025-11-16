package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.thejawnpaul.gptinvestor.core.utility.ShareService
import com.thejawnpaul.gptinvestor.core.utility.ToastDuration
import com.thejawnpaul.gptinvestor.core.utility.ToastManager
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetUpNavGraph(navController: NavHostController) {
    Scaffold { innerPadding ->
        val clipboard = LocalClipboard.current
        val toastManager by remember { mutableStateOf(ToastManager()) }
        val shareService by remember { mutableStateOf(ShareService()) }
        NavHost(
            navController = navController,
            startDestination = HomeTabScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeTabScreen> {
                val homeViewModel = koinViewModel<HomeViewModel>()
                val state = homeViewModel.uiState.collectAsStateWithLifecycle()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    homeViewModel.actions.onEach { action ->
                        when (action) {
                            HomeAction.OnGoToAllTopPicks -> {
                                navController.navigate(AllTopPicksScreen)
                            }

                            is HomeAction.OnGoToCompanyDetail -> {
                                navController.navigate(CompanyDetailScreen(action.ticker))
                            }

                            is HomeAction.OnGoToTopPickDetail -> {
                                navController.navigate(TopPickDetailScreen(action.id))
                            }

                            is HomeAction.OnStartConversation -> {
                                navController.navigate(
                                    ConversationScreen(
                                        chatInput = action.input ?: "",
                                        title = action.title
                                    )
                                )
                            }

                            HomeAction.OnGoToDiscover -> {
                                navController.navigate(DiscoverTabScreen)
                            }

                            HomeAction.OnGoToHistory -> {
                                navController.navigate(HistoryTabScreen)
                            }

                            HomeAction.OnGoToSavedPicks -> {
                                navController.navigate(SavedTopPicksScreen)
                            }

                            HomeAction.OnGoToSettings -> {
                                navController.navigate(SettingsScreen)
                            }

                            HomeAction.OnGoToAllTidbits -> {
                                navController.navigate(route = TidbitScreen)
                            }

                            is HomeAction.OnGoToTidbitDetail -> {
                                navController.navigate(
                                    route = TidbitDetailScreen(
                                        tidbitId = action.id
                                    )
                                )
                            }

                            HomeAction.OnGoToSavedTidbits -> {
                                navController.navigate(route = SavedTidbitScreen)
                            }
                        }
                    }.launchIn(scope)
                }

                HomeScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onEvent = homeViewModel::handleEvent,
                    onAction = homeViewModel::processAction
                )
            }
            composable<DiscoverTabScreen>(
                deepLinks = listOf(
                    navDeepLink<DiscoverTabScreen>(basePath = DiscoverTabScreen.deepLink)
                )
            ) {
                val companyViewModel = koinViewModel<CompanyViewModel>()
                val state = companyViewModel.companyDiscoveryState.collectAsStateWithLifecycle()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    companyViewModel.companyDiscoveryAction.onEach { action ->
                        when (action) {
                            is CompanyDiscoveryAction.OnNavigateToCompanyDetail -> {
                                navController.navigate(
                                    CompanyDetailScreen(
                                        action.ticker
                                    )
                                )
                            }

                            CompanyDiscoveryAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is CompanyDiscoveryAction.OnGoToPickDetail -> {
                                navController.navigate(
                                    TopPickDetailScreen(
                                        action.id
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
            composable<HistoryTabScreen> {
                val viewModel = koinViewModel<HistoryViewModel>()
                val state = viewModel.historyScreenViewState.collectAsStateWithLifecycle()
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            is HistoryScreenAction.OnGoToHistoryDetail -> {
                                navController.navigate(
                                    HistoryDetailScreen(action.conversationId)
                                )
                            }

                            HistoryScreenAction.OnGoBack -> {
                                navController.navigateUp()
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

            composable<CompanyDetailScreen> { backStackEntry ->
                val parentViewModel = koinViewModel<CompanyViewModel>()
                val detail = backStackEntry.toRoute<CompanyDetailScreen>()
                val ticker = detail.ticker
                val state = parentViewModel.selectedCompany.collectAsStateWithLifecycle()
                val scope = rememberCoroutineScope()
                val clipboard = LocalClipboard.current

                LaunchedEffect(Unit) {
                    parentViewModel.companyDetailAction.onEach { action ->
                        when (action) {
                            CompanyDetailAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is CompanyDetailAction.OnNavigateToWebView -> {
                                navController.navigate(WebViewScreen(action.url))
                            }

                            is CompanyDetailAction.OnCopy -> {
                                clipboard.setClipEntry(action.text.toClipEntry())
                                toastManager.showToast("Copied", ToastDuration.Short)
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

            composable<ConversationScreen> { navBackStackEntry ->
                val viewModel = koinViewModel<ConversationViewModel>()
                val state = viewModel.conversation.collectAsStateWithLifecycle()
                val conversation = navBackStackEntry.toRoute<ConversationScreen>()
                val chatInput = conversation.chatInput
                val title = conversation.title
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            ConversationAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is ConversationAction.OnGoToWebView -> {
                                navController.navigate(WebViewScreen(action.url))
                            }

                            is ConversationAction.OnCopy -> {
                                clipboard.setClipEntry(action.text.toClipEntry())
                                toastManager.showToast("Copied", ToastDuration.Short)
                            }
                        }
                    }.launchIn(scope)
                }

                ConversationScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    chatInput = chatInput,
                    title = title,
                    state = state.value,
                    onEvent = viewModel::handleEvent,
                    onAction = viewModel::processAction
                )
            }

            composable<WebViewScreen> { navBackStackEntry ->
                val data = navBackStackEntry.toRoute<WebViewScreen>().url
                WebViewScreen(url = data, onGoBack = {
                    navController.navigateUp()
                })
            }

            composable<HistoryDetailScreen> { navBackStackEntry ->
                val viewModel = koinViewModel<HistoryViewModel>()
                val state = viewModel.conversation.collectAsStateWithLifecycle()
                val history = navBackStackEntry.toRoute<HistoryDetailScreen>()
                val id = history.conversationId.toString()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.historyDetailAction.onEach { action ->
                        when (action) {
                            HistoryDetailAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is HistoryDetailAction.OnGoToWebView -> {
                                navController.navigate(WebViewScreen(action.url))
                            }

                            is HistoryDetailAction.OnCopy -> {
                                clipboard.setClipEntry(action.text.toClipEntry())
                                toastManager.showToast("Copied", ToastDuration.Short)
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

            composable<TopPickDetailScreen> { navBackStackEntry ->
                val viewModel = koinViewModel<TopPickViewModel>()
                val state = viewModel.topPickView.collectAsStateWithLifecycle()
                val pickDetail = navBackStackEntry.toRoute<TopPickDetailScreen>()
                val id = pickDetail.topPickId
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            TopPickAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is TopPickAction.OnShare -> {
                                shareService.showChooser("Top pick", action.url)
                            }

                            is TopPickAction.ShowToast -> {
                                toastManager.showToast(action.message, ToastDuration.Short)
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

            composable<AllTopPicksScreen> {
                val viewModel = koinViewModel<TopPickViewModel>()
                val state = viewModel.allTopPicks.collectAsStateWithLifecycle()
                AllTopPicksScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onGoBack = {
                        navController.navigateUp()
                    },
                    onGoToDetail = { id ->
                        navController.navigate(TopPickDetailScreen(id))
                    }
                )
            }

            composable<SavedTopPicksScreen> {
                val viewModel = koinViewModel<TopPickViewModel>()
                val state = viewModel.savedTopPicks.collectAsStateWithLifecycle()
                SavedTopPicksScreen(
                    modifier = Modifier.padding(top = 20.dp),
                    state = state.value,
                    onGoBack = {
                        navController.navigateUp()
                    },
                    onGoToDetail = { id ->
                        navController.navigate(TopPickDetailScreen(id))
                    }
                )
            }

            composable<SettingsScreen> {
                val viewModel = koinViewModel<SettingsViewModel>()
                val state = viewModel.uiState.collectAsStateWithLifecycle()
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

            composable<TidbitDetailScreen> { navBackStackEntry ->
                val detail = navBackStackEntry.toRoute<TidbitDetailScreen>()
                val tidbitId = detail.tidbitId
                val viewModel = koinViewModel<TidbitViewModel>()
                val state = viewModel.tidbitDetailState.collectAsStateWithLifecycle()
                val scope = rememberCoroutineScope()
                val uriHandler = LocalUriHandler.current
                LaunchedEffect(Unit) {
                    viewModel.tidbitDetailActions.onEach { action ->
                        when (action) {
                            TidbitDetailAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is TidbitDetailAction.OnOpenSource -> {
                                try {
                                    uriHandler.openUri(action.url)
                                } catch (e: Exception) {
                                    // Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show()
                                }
                            }

                            is TidbitDetailAction.OnShare -> {
                                shareService.showChooser("Tidbit", action.shareText)
                            }
                        }
                    }.launchIn(scope)
                }

                TidbitDetailScreen(
                    modifier = Modifier,
                    tidbitId = tidbitId,
                    onEvent = viewModel::handleDetailEvent,
                    state = state.value,
                    onAction = viewModel::handleDetailAction
                )
            }

            composable<TidbitScreen> {
                val viewModel = koinViewModel<TidbitViewModel>()
                val state = viewModel.tidbitMainScreenState.collectAsStateWithLifecycle()
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    viewModel.actions.onEach { action ->
                        when (action) {
                            TidbitAction.OnGoBack -> {
                                navController.navigateUp()
                            }

                            is TidbitAction.OnShare -> {
                                shareService.showChooser("Tidbit", action.shareText)
                            }

                            is TidbitAction.OnGoToTidbitDetail -> {
                                navController.navigate(
                                    TidbitDetailScreen(tidbitId = action.tidbitId)
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

            composable<SavedTidbitScreen> {
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
                                    route = TidbitDetailScreen(tidbitId = action.tidbitId)
                                )
                            }

                            is TidbitAction.OnShare -> {
                                shareService.showChooser("Tidbit", action.shareText)
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
        }
    }
}
