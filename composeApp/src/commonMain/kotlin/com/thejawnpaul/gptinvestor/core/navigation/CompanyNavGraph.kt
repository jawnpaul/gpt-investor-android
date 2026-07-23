package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.added_to_watchlist_error
import com.thejawnpaul.gptinvestor.added_to_watchlist_success
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.WebViewScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.share_brief_text
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.companyNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(route = Screen.CompanyDetailScreen.route) {
        val parentViewModel = koinViewModel<CompanyViewModel>()
        val state = parentViewModel.selectedCompany.collectAsState()

        LaunchedEffect(Unit) {
            parentViewModel.companyDetailAction.collect { action ->
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
