package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.profile.presentation.ui.ProfileScreen
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileAction
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.profileNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(Screen.ProfileTabScreen.route) {
        val viewModel: ProfileViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    ProfileAction.NavigateToAppearance -> {
                    }
                    ProfileAction.NavigateToHistory -> {
                        navController.navigate(
                            route = Screen.HistoryTabScreen.route
                        )
                    }
                    ProfileAction.NavigateToNotifications -> {
                    }
                    ProfileAction.NavigateToPrivacy -> {
                        platformActions.openUrl(Constants.PRIVACY_POLICY_URL)
                    }
                    ProfileAction.NavigateToSavedPicks -> {
                        navController.navigate(
                            route = Screen.SavedTopPicksScreen.route
                        )
                    }
                    ProfileAction.NavigateToSavedTidbits -> {
                        navController.navigate(
                            route = Screen.SavedTidbitScreen.route
                        )
                    }
                    ProfileAction.NavigateToUpgrade -> {
                    }

                    ProfileAction.NavigateToSettings -> {
                        navController.navigate(route = Screen.SettingsScreen.route)
                    }

                    is ProfileAction.ShowToast -> {
                    }
                }
            }.launchIn(scope)
        }

        ProfileScreen(
            state = state,
            onEvent = viewModel::handleEvent
        )
    }
}
