package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.thejawnpaul.gptinvestor.features.discover.DISCOVER_PAGE_COUNT
import com.thejawnpaul.gptinvestor.features.discover.DISCOVER_SECTION_KEY
import com.thejawnpaul.gptinvestor.features.discover.DiscoverPicksTab
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.discover.DiscoverSection
import com.thejawnpaul.gptinvestor.features.discover.DiscoverTabsViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoverViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoveryAction
import com.thejawnpaul.gptinvestor.features.discover.DiscoveryEvent
import com.thejawnpaul.gptinvestor.features.discover.DiscoveryScreenState
import com.thejawnpaul.gptinvestor.features.discover.NEWS_PAGE
import com.thejawnpaul.gptinvestor.features.discover.PICKS_PAGE
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import com.thejawnpaul.gptinvestor.features.news.presentation.NewsFeedEvent
import com.thejawnpaul.gptinvestor.features.news.presentation.NewsFeedViewModel
import com.thejawnpaul.gptinvestor.features.news.presentation.ui.NewsTab
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.discoverNavGraph(navController: NavHostController) {
    composable(route = Screen.DiscoverTabScreen.route) { backStackEntry ->
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

                    DiscoveryAction.OnGoToLogin -> {
                        navController.navigateClearingBackStack(Screen.LoginScreen.route)
                    }

                    DiscoveryAction.OnGoToSearch -> {
                        navController.navigate(route = Screen.SearchScreen.route)
                    }

                    is DiscoveryAction.OnGoToTidbitDetail -> {
                        navController.navigate(route = Screen.TidbitDetailScreen.createRoute(tidbitId = action.id))
                    }

                    DiscoveryAction.OnGoToBrowse -> {
                        navController.navigate(Screen.BrowseStocksScreen.createRoute())
                    }

                    DiscoveryAction.OnGoToAllTopPicks -> {
                        navController.navigate(Screen.AllTopPicksScreen.route)
                    }
                }
            }.launchIn(scope)
        }

        DiscoverTabsDestination(
            navController = navController,
            backStackEntry = backStackEntry,
            isGuest = false,
            picksState = state.value,
            onPicksEvent = viewModel::handleEvent
        )
    }
}

internal fun NavController.navigateToDiscover(section: DiscoverSection, isGuest: Boolean) {
    val discoverRoute = if (isGuest) GuestScreen.GuestDiscoverTab.route else Screen.DiscoverTabScreen.route
    if (isGuest) {
        navigateToTab(GuestScreen.GuestDiscoverTab.createRoute(), GuestScreen.GuestHomeTab.route)
    } else {
        navigateToTab(Screen.DiscoverTabScreen.route, Screen.HomeTabScreen.route)
    }
    popBackStack(discoverRoute, inclusive = false)
    getBackStackEntry(discoverRoute).savedStateHandle[DISCOVER_SECTION_KEY] = section.name
}

@Composable
internal fun DiscoverTabsDestination(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    isGuest: Boolean,
    picksState: DiscoveryScreenState,
    onPicksEvent: (DiscoveryEvent) -> Unit
) {
    val newsViewModel = koinViewModel<NewsFeedViewModel>()
    val newsState by newsViewModel.state.collectAsState()
    val stories = newsViewModel.stories.collectAsLazyPagingItems()
    val tabsViewModel = koinViewModel<DiscoverTabsViewModel>()
    val entryState = backStackEntry.savedStateHandle
    val requestedSection by remember(entryState) {
        entryState.getStateFlow<String?>(DISCOVER_SECTION_KEY, null)
    }.collectAsState()
    val pagerState = rememberPagerState { DISCOVER_PAGE_COUNT }
    var requestedPage by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        newsViewModel.actions.collect { handleNewsFeedAction(it, navController, isGuest) }
    }
    LaunchedEffect(requestedSection) {
        val section = DiscoverSection.entries.firstOrNull { it.name == requestedSection } ?: return@LaunchedEffect
        val page = if (section == DiscoverSection.NEWS) NEWS_PAGE else PICKS_PAGE
        if (page != pagerState.settledPage) requestedPage = page
        pagerState.scrollToPage(page)
        entryState[DISCOVER_SECTION_KEY] = null
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.drop(1).collect { page ->
            if (page == requestedPage) {
                requestedPage = null
                return@collect
            }
            tabsViewModel.onSectionSelected(
                section = if (page == NEWS_PAGE) DiscoverSection.NEWS else DiscoverSection.PICKS,
                isGuest = isGuest
            )
        }
    }
    LaunchedEffect(pagerState.settledPage, newsState.latestPublishedAt) {
        if (pagerState.settledPage == NEWS_PAGE) newsViewModel.handleEvent(NewsFeedEvent.MarkSeen)
    }

    DiscoverScreen(
        pagerState = pagerState,
        isGuestSession = isGuest,
        hasNewStories = newsState.hasNewStories,
        onGoToLogin = { onPicksEvent(DiscoveryEvent.GoToLogin) },
        picks = { DiscoverPicksTab(state = picksState, onEvent = onPicksEvent) },
        news = { NewsTab(state = newsState, stories = stories, onEvent = newsViewModel::handleEvent) }
    )
}
