package com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.RiskTolerance
import kotlinx.coroutines.flow.Flow

interface IPostAuthOnboardingRepository {
    fun saveOnboardingAnswers(
        experience: InvestingExperience?,
        goal: InvestingGoal?,
        riskTolerance: RiskTolerance?
    ): Flow<Either<Failure, Unit>>
}
