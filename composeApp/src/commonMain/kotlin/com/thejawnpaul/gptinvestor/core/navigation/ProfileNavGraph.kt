package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.profile.presentation.ui.ProfileScreen
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    composable(Screen.ProfileTabScreen.route) {
        val viewModel: ProfileViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        ProfileScreen(
            state = state,
            onEvent = viewModel::handleEvent
        )
    }
}
