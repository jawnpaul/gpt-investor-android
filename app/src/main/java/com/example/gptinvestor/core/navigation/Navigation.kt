package com.example.gptinvestor.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.example.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.example.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel

@Composable
fun SetUpNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(modifier = Modifier, navController = navController, viewModel)
        }

        composable(route = Screen.CompanyDetailScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.HomeScreen.route)
            }
            val parentViewModel = hiltViewModel<HomeViewModel>(parentEntry)

            CompanyDetailScreen(
                modifier = Modifier,
                navController = navController,
                viewModel = parentViewModel
            )
        }
    }
}
