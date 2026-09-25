package com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.RiskTolerance
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.usecases.SaveOnboardingAnswersParams
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.usecases.SaveOnboardingAnswersUseCase
import com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.state.PostAuthOnboardingUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

sealed interface PostAuthOnboardingAction {
    data object NavigateToHome : PostAuthOnboardingAction
}

@KoinViewModel
class PostAuthOnboardingViewModel(
    private val saveOnboardingAnswersUseCase: SaveOnboardingAnswersUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostAuthOnboardingUiState())
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<PostAuthOnboardingAction>()
    val actions get() = _actions

    fun onExperienceSelected(experience: InvestingExperience) {
        _uiState.update { it.copy(experience = experience) }
    }

    fun onGoalSelected(goal: InvestingGoal) {
        _uiState.update { it.copy(goal = goal) }
    }

    fun onRiskToleranceSelected(riskTolerance: RiskTolerance) {
        _uiState.update { it.copy(riskTolerance = riskTolerance) }
    }

    fun onContinue() {
        if (_uiState.value.currentStep == 0) {
            _uiState.update { it.copy(currentStep = 1, error = null) }
            return
        }
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }
        saveOnboardingAnswersUseCase(
            SaveOnboardingAnswersParams(
                experience = state.experience,
                goal = state.goal,
                riskTolerance = state.riskTolerance
            )
        ) { result ->
            _uiState.update { it.copy(isLoading = false) }
            when (result) {
                is Either.Right -> markCompletedAndNavigate()
                is Either.Left -> _uiState.update { it.copy(error = "Something went wrong.") }
            }
        }
    }

    fun onBack() {
        if (_uiState.value.currentStep > 0) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1, error = null) }
        }
    }

    fun onSkip() {
        markCompletedAndNavigate()
    }

    private fun markCompletedAndNavigate() {
        viewModelScope.launch {
            appPreferences.setHasCompletedPostAuthOnboarding(true)
            _actions.emit(PostAuthOnboardingAction.NavigateToHome)
        }
    }
}
