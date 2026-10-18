package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.ui.PostAuthOnboardingScreen
import com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.viewmodel.PostAuthOnboardingAction
import com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.viewmodel.PostAuthOnboardingViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.postAuthOnboardingNavGraph(navController: NavHostController) {
    composable(Screen.PostAuthOnboardingScreen.route) {
        val viewModel = koinViewModel<PostAuthOnboardingViewModel>()
        val state = viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    PostAuthOnboardingAction.NavigateToHome -> {
                        navController.navigate(Screen.HomeTabScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }.launchIn(scope)
        }

        PostAuthOnboardingScreen(
            state = state.value,
            onSelectExperience = viewModel::onExperienceSelected,
            onSelectGoal = viewModel::onGoalSelected,
            onSelectRiskTolerance = viewModel::onRiskToleranceSelected,
            onContinue = viewModel::onContinue,
            onSkip = viewModel::onSkip,
            onBack = viewModel::onBack
        )
    }
}
