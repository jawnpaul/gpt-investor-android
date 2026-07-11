package com.thejawnpaul.gptinvestor.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui.WatchlistScreen

fun NavGraphBuilder.watchlistNavGraph(navController: NavHostController) {
    composable(Screen.WatchlistTabScreen.route) {
        WatchlistScreen()
    }
}
