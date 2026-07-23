package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.added_to_watchlist_error
import com.thejawnpaul.gptinvestor.added_to_watchlist_success
import com.thejawnpaul.gptinvestor.share_brief_text
import org.jetbrains.compose.resources.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.WebViewScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.companyNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(route = Screen.CompanyDetailScreen.route) {
        val parentViewModel = koinViewModel<CompanyViewModel>()
        val state = parentViewModel.selectedCompany.collectAsState()
        val scope = rememberCoroutineScope()
        val shareBriefTemplate = stringResource(Res.string.share_brief_text)
        val watchlistAddedMessage = stringResource(Res.string.added_to_watchlist_success)
        val watchlistAddFailedMessage = stringResource(Res.string.added_to_watchlist_error)

        LaunchedEffect(Unit) {
            parentViewModel.companyDetailAction.onEach { action ->
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
                        platformActions.shareText(shareBriefTemplate.format(action.name, action.ticker, action.id))
                    }

                    CompanyDetailAction.WatchlistAdded -> {
                        platformActions.showMessage(watchlistAddedMessage)
                    }

                    CompanyDetailAction.WatchlistAddFailed -> {
                        platformActions.showMessage(watchlistAddFailedMessage)
                    }
                }
            }.launchIn(scope)
        }

        CompanyDetailScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = parentViewModel::handleCompanyDetailEvent,
            onAction = parentViewModel::processCompanyDetailAction
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
}
