package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.features.authentication.presentation.AuthenticationScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.AuthenticationViewModel
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.WebViewScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.discover.DiscoverScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryDetailScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.ui.HistoryScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.HomeScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsScreen
import com.thejawnpaul.gptinvestor.features.settings.presentation.SettingsViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.AllTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.SavedTopPicksScreen
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.TopPickDetailScreen
import kotlinx.coroutines.launch

@Composable
fun SetUpNavGraph(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavDrawerContent(
                navController = navController,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = { BottomNavBar(navController) }
        ) { innerPadding ->
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
                        viewModel = homeViewModel,
                        onMenuClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }
                composable(Screen.DiscoverTabScreen.route) {
                    val companyViewModel = hiltViewModel<CompanyViewModel>()
                    DiscoverScreen(
                        modifier = Modifier,
                        navController = navController,
                        companyViewModel = companyViewModel
                    )
                }
                composable(Screen.HistoryTabScreen.route) {
                    val viewModel = hiltViewModel<HistoryViewModel>()
                    HistoryScreen(
                        modifier = Modifier,
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                composable(route = Screen.CompanyDetailScreen.route) { backStackEntry ->
                    val parentViewModel = hiltViewModel<CompanyViewModel>()
                    val ticker = backStackEntry.arguments?.getString("ticker") ?: ""
                    CompanyDetailScreen(
                        modifier = Modifier,
                        navController = navController,
                        viewModel = parentViewModel,
                        ticker = ticker
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

                composable(
                    route = Screen.HistoryDetailScreen.route,
                    arguments = listOf(navArgument("conversationId") { NavType.StringType })
                ) { navBackStackEntry ->
                    val viewModel = hiltViewModel<HistoryViewModel>()
                    val id = navBackStackEntry.arguments?.getString("conversationId") ?: ""

                    HistoryDetailScreen(
                        modifier = Modifier,
                        navController = navController,
                        conversationId = id,
                        viewModel = viewModel
                    )
                }

                composable(
                    route = Screen.TopPickDetailScreen.route,
                    arguments = listOf(navArgument("topPickId") { NavType.StringType })
                ) { navBackStackEntry ->
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    val id = navBackStackEntry.arguments?.getString("topPickId") ?: ""
                    TopPickDetailScreen(
                        modifier = Modifier,
                        navController = navController,
                        topPickId = id,
                        viewModel = viewModel
                    )
                }

                composable(route = Screen.AllTopPicksScreen.route) {
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    AllTopPicksScreen(
                        modifier = Modifier,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable(route = Screen.AuthenticationScreen.route) {
                    val viewModel = hiltViewModel<AuthenticationViewModel>()
                    AuthenticationScreen(
                        viewModel = viewModel,
                        onSignInSuccess = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(route = Screen.SavedTopPicksScreen.route) {
                    val viewModel = hiltViewModel<TopPickViewModel>()
                    SavedTopPicksScreen(
                        modifier = Modifier,
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                composable(route = Screen.SettingsScreen.route) {
                    val viewModel = hiltViewModel<SettingsViewModel>()
                    SettingsScreen(
                        modifier = Modifier,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
