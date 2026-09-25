package com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.state

import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.RiskTolerance

data class PostAuthOnboardingUiState(
    val currentStep: Int = 0,
    val experience: InvestingExperience? = null,
    val goal: InvestingGoal? = null,
    val riskTolerance: RiskTolerance? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
