package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.added_to_watchlist_error
import com.thejawnpaul.gptinvestor.added_to_watchlist_success
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.failed_to_remove_from_watchlist
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import com.thejawnpaul.gptinvestor.features.news.presentation.ExplainerAction
import com.thejawnpaul.gptinvestor.features.news.presentation.ExplainerEvent
import com.thejawnpaul.gptinvestor.features.news.presentation.ExplainerViewModel
import com.thejawnpaul.gptinvestor.features.news.presentation.NewsFeedAction
import com.thejawnpaul.gptinvestor.features.news.presentation.StockWatchEvent
import com.thejawnpaul.gptinvestor.features.news.presentation.StoryAction
import com.thejawnpaul.gptinvestor.features.news.presentation.StoryEvent
import com.thejawnpaul.gptinvestor.features.news.presentation.StoryViewModel
import com.thejawnpaul.gptinvestor.features.news.presentation.ui.ExplainerScreen
import com.thejawnpaul.gptinvestor.features.news.presentation.ui.StoryScreen
import com.thejawnpaul.gptinvestor.login
import com.thejawnpaul.gptinvestor.news_added_all
import com.thejawnpaul.gptinvestor.news_ask_stock_prompt
import com.thejawnpaul.gptinvestor.news_ask_story_prompt
import com.thejawnpaul.gptinvestor.news_share_text
import com.thejawnpaul.gptinvestor.news_undo_failed
import com.thejawnpaul.gptinvestor.removed_from_watchlist
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiToastResult
import com.thejawnpaul.gptinvestor.theme.components.GiToastState
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.undo
import com.thejawnpaul.gptinvestor.view
import com.thejawnpaul.gptinvestor.watchlist_sign_in_required
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.newsNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(route = Screen.StoryScreen.route) {
        StoryDestination(navController, platformActions, isGuest = false)
    }
    composable(route = GuestScreen.GuestStory.route) {
        StoryDestination(navController, platformActions, isGuest = true)
    }
    composable(route = Screen.ExplainerScreen.route) {
        ExplainerDestination(navController, platformActions, isGuest = false)
    }
    composable(route = GuestScreen.GuestExplainer.route) {
        ExplainerDestination(navController, platformActions, isGuest = true)
    }
}

@Composable
private fun StoryDestination(navController: NavHostController, platformActions: PlatformActions, isGuest: Boolean) {
    val viewModel = koinViewModel<StoryViewModel>()
    val state by viewModel.state.collectAsState()
    val toastState = rememberGiToastState()
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
            when (action) {
                StoryAction.GoBack -> navController.navigateUp()
                is StoryAction.OpenCompany -> navController.openCompany(action.ticker, isGuest)
                is StoryAction.OpenChat -> navController.openChat(
                    input = action.stock?.let { getString(Res.string.news_ask_stock_prompt, it.name) }
                        ?: getString(Res.string.news_ask_story_prompt),
                    title = action.title,
                    isGuest = isGuest,
                    ticker = action.stock?.ticker,
                    storyId = action.storyId
                )
                is StoryAction.OpenExplainer -> navController.navigate(
                    if (isGuest) {
                        GuestScreen.GuestExplainer.createRoute(action.storyId)
                    } else {
                        Screen.ExplainerScreen.createRoute(action.storyId)
                    }
                )

                is StoryAction.OpenUrl -> platformActions.openUrl(action.url)
                is StoryAction.ShareImage -> platformActions.shareImage(
                    png = action.png,
                    text = getString(Res.string.news_share_text, action.story.title, action.url)
                )

                is StoryAction.ShareText -> platformActions.shareText(
                    getString(Res.string.news_share_text, action.story.title, action.url)
                )

                is StoryAction.Watch -> launch {
                    showWatchToast(action.event, toastState, haptics, navController, isGuest) { tickers ->
                        viewModel.handleEvent(StoryEvent.UndoWatchAll(tickers))
                    }
                }
            }
        }
    }
    StoryScreen(state = state, onEvent = viewModel::handleEvent, toastState = toastState)
}

@Composable
private fun ExplainerDestination(navController: NavHostController, platformActions: PlatformActions, isGuest: Boolean) {
    val viewModel = koinViewModel<ExplainerViewModel>()
    val state by viewModel.state.collectAsState()
    val toastState = rememberGiToastState()
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
            when (action) {
                ExplainerAction.GoBack -> navController.navigateUp()
                is ExplainerAction.OpenCompany -> navController.openCompany(action.ticker, isGuest)
                is ExplainerAction.OpenUrl -> platformActions.openUrl(action.url)
                is ExplainerAction.Share -> platformActions.shareText(
                    getString(Res.string.news_share_text, action.title, action.link)
                )

                is ExplainerAction.Watch -> launch {
                    showWatchToast(action.event, toastState, haptics, navController, isGuest) { tickers ->
                        viewModel.handleEvent(ExplainerEvent.UndoWatchAll(tickers))
                    }
                }
            }
        }
    }
    ExplainerScreen(state = state, onEvent = viewModel::handleEvent, toastState = toastState)
}

private suspend fun showWatchToast(
    event: StockWatchEvent,
    toastState: GiToastState,
    haptics: HapticFeedback,
    navController: NavHostController,
    isGuest: Boolean,
    onUndo: (List<String>) -> Unit
) {
    when (event) {
        is StockWatchEvent.Added -> {
            haptics.performHapticFeedback(GiHaptics.Confirm)
            val result = toastState.show(
                message = getString(Res.string.added_to_watchlist_success),
                icon = GiIcons.check,
                actionText = getString(Res.string.view)
            )
            if (result == GiToastResult.ActionPerformed) navController.openWatchlist(isGuest)
        }

        is StockWatchEvent.AddedAll -> {
            haptics.performHapticFeedback(GiHaptics.Confirm)
            val result = toastState.show(
                message = getPluralString(Res.plurals.news_added_all, event.tickers.size, event.tickers.size),
                icon = GiIcons.check,
                actionText = getString(Res.string.undo)
            )
            if (result == GiToastResult.ActionPerformed) onUndo(event.tickers)
        }

        is StockWatchEvent.Removed -> toastState.show(
            message = getString(Res.string.removed_from_watchlist, event.ticker)
        )

        StockWatchEvent.AddFailed -> {
            haptics.performHapticFeedback(GiHaptics.Reject)
            toastState.show(message = getString(Res.string.added_to_watchlist_error))
        }

        is StockWatchEvent.RemoveFailed -> {
            haptics.performHapticFeedback(GiHaptics.Reject)
            toastState.show(message = getString(Res.string.failed_to_remove_from_watchlist, event.ticker))
        }

        StockWatchEvent.UndoFailed -> {
            haptics.performHapticFeedback(GiHaptics.Reject)
            toastState.show(message = getString(Res.string.news_undo_failed))
        }

        StockWatchEvent.SignInRequired -> {
            val result = toastState.show(
                message = getString(Res.string.watchlist_sign_in_required),
                icon = GiIcons.star,
                actionText = getString(Res.string.login)
            )
            if (result ==
                GiToastResult.ActionPerformed
            ) {
                navController.navigateClearingBackStack(Screen.LoginScreen.route)
            }
        }
    }
}

private fun NavHostController.openCompany(ticker: String, isGuest: Boolean) = navigate(
    if (isGuest) GuestScreen.GuestCompanyDetail.createRoute(ticker) else Screen.CompanyDetailScreen.createRoute(ticker)
)

private fun NavHostController.openChat(
    input: String,
    title: String?,
    isGuest: Boolean,
    ticker: String? = null,
    storyId: String? = null
) = navigate(
    if (isGuest) {
        GuestScreen.GuestConversation.createRoute(chatInput = input, title = title, ticker = ticker, storyId = storyId)
    } else {
        Screen.ConversationScreen.createRoute(chatInput = input, title = title, ticker = ticker, storyId = storyId)
    }
)

internal fun NavHostController.openStory(id: String, isGuest: Boolean) = navigate(
    if (isGuest) GuestScreen.GuestStory.createRoute(id) else Screen.StoryScreen.createRoute(id)
)

private fun NavHostController.openWatchlist(isGuest: Boolean) = if (isGuest) {
    navigateToTab(GuestScreen.GuestWatchlistTab.route, GuestScreen.GuestHomeTab.route)
} else {
    navigateToTab(Screen.WatchlistTabScreen.route, Screen.HomeTabScreen.route)
}

internal fun handleNewsFeedAction(action: NewsFeedAction, navController: NavHostController, isGuest: Boolean) {
    when (action) {
        is NewsFeedAction.OpenStory -> navController.openStory(action.id, isGuest)
        NewsFeedAction.OpenChat -> navController.openChat(input = "", title = null, isGuest = isGuest)
        NewsFeedAction.OpenWatchlist -> navController.openWatchlist(isGuest)
        NewsFeedAction.OpenBrowse -> navController.navigate(
            if (isGuest) GuestScreen.GuestBrowseStocks.createRoute() else Screen.BrowseStocksScreen.createRoute()
        )
    }
}
