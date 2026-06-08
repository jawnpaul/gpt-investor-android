package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.OnboardingScreen
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.viewmodel.OnboardingAction
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.viewmodel.OnboardingViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController) {
    composable(Screen.OnboardingScreen.route) {
        val viewModel = koinViewModel<OnboardingViewModel>()
        val state = viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    OnboardingAction.NavigateToHome -> {
                        navController.navigate(Screen.DefaultAuthenticationScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }.launchIn(scope)
        }
        OnboardingScreen(
            state = state.value,
            searchResults = viewModel.searchResults,
            onNextScreen = viewModel::onNextScreen,
            onSkip = viewModel::onSkip,
            onSelectStock = viewModel::onStockSelected,
            onSearchQueryChange = viewModel::onSearchQueryChanged,
            onBackToStockSelection = viewModel::onBackToStockSelection,
            onFinish = viewModel::onFinish
        )
    }
}
