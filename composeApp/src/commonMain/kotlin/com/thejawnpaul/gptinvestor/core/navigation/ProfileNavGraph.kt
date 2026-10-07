package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import com.thejawnpaul.gptinvestor.features.profile.presentation.ui.ProfileScreen
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileAction
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.profileNavGraph(navController: NavHostController, platformActions: PlatformActions) {
    composable(Screen.ProfileTabScreen.route) {
        ProfileTab(navController, platformActions)
    }
    composable(GuestScreen.GuestProfileTab.route) {
        ProfileTab(navController, platformActions)
    }
}

@Composable
private fun ProfileTab(navController: NavHostController, platformActions: PlatformActions) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
            when (action) {
                ProfileAction.OnGoToHistory -> navController.navigate(Screen.HistoryTabScreen.route)
                ProfileAction.OnGoToSavedPicks -> navController.navigate(Screen.SavedTopPicksScreen.route)
                ProfileAction.OnGoToSavedTidbits -> navController.navigate(Screen.SavedTidbitScreen.route)
                ProfileAction.OnGoToSettings -> navController.navigate(Screen.SettingsScreen.route)
                is ProfileAction.ShowToast -> platformActions.showMessage(action.message)
                ProfileAction.OnGoToSignUp -> navController.navigate(Screen.SignUpScreen.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }

    ProfileScreen(
        modifier = Modifier,
        state = state,
        onEvent = viewModel::handleEvent
    )
}
