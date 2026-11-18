package com.thejawnpaul.gptinvestor.features.toppick.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingQuestionRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.AllTopPicksUiState
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AllTopPicksViewModel(
    private val getTopPicksUseCase: GetTopPicksUseCase,
    onboardingQuestionRepository: IOnboardingQuestionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AllTopPicksUiState())
    val state = combine(
        _state,
        onboardingQuestionRepository.getQuestionState(OnboardingQuestion.GOAL)
    ) { state, goal ->
        state.copy(savedGoal = InvestingGoal.fromAnswer(goal?.answer))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AllTopPicksUiState(topPicksView = TopPicksView(loading = true))
    )

    init {
        getTopPicks()
    }

    fun handleEvent(event: AllTopPicksEvent) {
        when (event) {
            is AllTopPicksEvent.SelectGoal -> _state.update { it.copy(selectedGoal = event.goal) }
            AllTopPicksEvent.Retry -> getTopPicks()
        }
    }

    private fun getTopPicks() {
        _state.update { it.copy(topPicksView = it.topPicksView.copy(loading = true, error = null)) }
        getTopPicksUseCase(GetTopPicksUseCase.None()) { result ->
            result.onFailure {
                _state.update {
                    it.copy(topPicksView = it.topPicksView.copy(loading = false, error = "Something went wrong"))
                }
            }
            result.onSuccess { picks ->
                _state.update { state ->
                    state.copy(
                        topPicksView = state.topPicksView.copy(
                            loading = false,
                            error = null,
                            topPicks = picks.map { pick ->
                                TopPickPresentation(
                                    id = pick.id,
                                    companyName = pick.companyName,
                                    ticker = pick.ticker,
                                    rationale = pick.rationale,
                                    metrics = pick.metrics,
                                    risks = pick.risks,
                                    confidenceScore = pick.confidenceScore,
                                    isSaved = pick.isSaved,
                                    percentageChange = pick.percentageChange,
                                    imageUrl = pick.imageUrl,
                                    currentPrice = pick.currentPrice,
                                    goalTags = pick.goalTags
                                )
                            }
                        )
                    )
                }
            }
        }
    }
}

sealed interface AllTopPicksEvent {
    data class SelectGoal(val goal: InvestingGoal) : AllTopPicksEvent
    data object Retry : AllTopPicksEvent
}
