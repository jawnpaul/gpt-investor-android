package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.WebViewScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.history.HistoryScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.BottomNavBar
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeBackground
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel

@Composable
fun SetUpNavGraph(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        HomeBackground(modifier = Modifier.fillMaxSize())
        NavHost(
            navController = navController,
            startDestination = Screen.HomeTabScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.HomeTabScreen.route) {
                val homeViewModel = hiltViewModel<HomeViewModel>()
                val companyViewModel = hiltViewModel<CompanyViewModel>()
                HomeScreen(
                    modifier = Modifier,
                    navController = navController,
                    viewModel = homeViewModel
                )
            }
            composable(Screen.DiscoverTabScreen.route) {
                val homeViewModel = hiltViewModel<HomeViewModel>()
                val companyViewModel = hiltViewModel<CompanyViewModel>()
                DiscoverScreen(
                    modifier = Modifier,
                    navController = navController,
                    homeViewModel = homeViewModel,
                    companyViewModel = companyViewModel
                )
            }
            composable(Screen.HistoryTabScreen.route) { HistoryScreen() }

            composable(route = Screen.CompanyDetailScreen.route) { backStackEntry ->

                val parentViewModel = hiltViewModel<CompanyViewModel>()

                CompanyDetailScreen(
                    modifier = Modifier,
                    navController = navController,
                    viewModel = parentViewModel
                )
            }

            composable(route = Screen.ConversationScreen.route) {
                val viewModel = hiltViewModel<ConversationViewModel>()

                ConversationScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    navController = navController
                )
            }

            composable(route = Screen.WebViewScreen.route) { navBackStackEntry ->

                val data = navBackStackEntry.arguments?.getString("url") ?: ""
                WebViewScreen(navController = navController, url = data)
            }
        }
    }
}
