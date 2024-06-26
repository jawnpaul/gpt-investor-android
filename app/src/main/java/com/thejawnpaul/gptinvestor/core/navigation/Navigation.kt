package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.WebViewScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel

@Composable
fun SetUpNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val companyViewModel = hiltViewModel<CompanyViewModel>()
            HomeScreen(
                modifier = Modifier,
                navController = navController,
                homeViewModel = homeViewModel,
                companyViewModel = companyViewModel
            )
        }

        composable(route = Screen.CompanyDetailScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.HomeScreen.route)
            }
            val parentViewModel = hiltViewModel<CompanyViewModel>(parentEntry)

            CompanyDetailScreen(
                modifier = Modifier,
                navController = navController,
                viewModel = parentViewModel
            )
        }

        composable(
            route = Screen.WebViewScreen.route
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.HomeScreen.route)
            }
            val parentViewModel = hiltViewModel<CompanyViewModel>(parentEntry)
            WebViewScreen(navController = navController, viewModel = parentViewModel)
        }
    }
}
