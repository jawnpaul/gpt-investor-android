package com.thejawnpaul.gptinvestor.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.profile.presentation.ui.ProfileScreen

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    composable(Screen.ProfileTabScreen.route) {
        ProfileScreen()
    }
}
