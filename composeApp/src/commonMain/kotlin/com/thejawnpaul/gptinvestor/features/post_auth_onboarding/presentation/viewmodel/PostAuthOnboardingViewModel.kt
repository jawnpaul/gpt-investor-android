package com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
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
import org.koin.core.annotation.Provided

sealed interface PostAuthOnboardingAction {
    data object NavigateToHome : PostAuthOnboardingAction
}

@KoinViewModel
class PostAuthOnboardingViewModel(
    private val saveOnboardingAnswersUseCase: SaveOnboardingAnswersUseCase,
    private val appPreferences: AppPreferences,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostAuthOnboardingUiState())
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<PostAuthOnboardingAction>()
    val actions get() = _actions

    init {
        analyticsLogger.logEvent("post-auth-onboarding-viewed", emptyMap())
        analyticsLogger.logEvent("post-auth-onboarding-step-viewed", mapOf("step" to 0))
    }

    fun onExperienceSelected(experience: InvestingExperience) {
        _uiState.update { it.copy(experience = experience) }
        analyticsLogger.logEvent("post-auth-onboarding-experience-selected", mapOf("experience" to experience.name))
    }

    fun onGoalSelected(goal: InvestingGoal) {
        _uiState.update { it.copy(goal = goal) }
        analyticsLogger.logEvent("post-auth-onboarding-goal-selected", mapOf("goal" to goal.name))
    }

    fun onRiskToleranceSelected(riskTolerance: RiskTolerance) {
        _uiState.update { it.copy(riskTolerance = riskTolerance) }
        analyticsLogger.logEvent(
            "post-auth-onboarding-risk-tolerance-selected",
            mapOf(
                "risk_tolerance" to riskTolerance.name
            )
        )
    }

    fun onContinue() {
        if (_uiState.value.currentStep == 0) {
            _uiState.update { it.copy(currentStep = 1, error = null) }
            analyticsLogger.logEvent("post-auth-onboarding-continued", mapOf("from_step" to 0))
            analyticsLogger.logEvent("post-auth-onboarding-step-viewed", mapOf("step" to 1))
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
                is Either.Right -> markCompletedAndNavigate(skipped = false)
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
        analyticsLogger.logEvent("post-auth-onboarding-skipped", mapOf("step" to _uiState.value.currentStep))
        markCompletedAndNavigate(skipped = true)
    }

    private fun markCompletedAndNavigate(skipped: Boolean) {
        if (!skipped) {
            val state = _uiState.value
            analyticsLogger.logEvent(
                "post-auth-onboarding-completed",
                mapOf(
                    "experience" to (state.experience?.name ?: ""),
                    "goal" to (state.goal?.name ?: ""),
                    "risk_tolerance" to (state.riskTolerance?.name ?: "")
                )
            )
        }
        viewModelScope.launch {
            appPreferences.setHasCompletedPostAuthOnboarding(true)
            _actions.emit(PostAuthOnboardingAction.NavigateToHome)
        }
    }
}
