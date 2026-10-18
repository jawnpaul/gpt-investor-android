package com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.RiskTolerance
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.repository.IPostAuthOnboardingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

data class SaveOnboardingAnswersParams(
    val experience: InvestingExperience?,
    val goal: InvestingGoal?,
    val riskTolerance: RiskTolerance?
)

@Factory
class SaveOnboardingAnswersUseCase(
    @Provided @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    @Provided coroutineScope: CoroutineScope,
    private val repository: IPostAuthOnboardingRepository
) : BaseUseCase<SaveOnboardingAnswersParams, Unit>(coroutineScope, dispatcher) {

    override suspend fun run(params: SaveOnboardingAnswersParams): Flow<Either<Failure, Unit>> =
        repository.saveOnboardingAnswers(
            experience = params.experience,
            goal = params.goal,
            riskTolerance = params.riskTolerance
        )
}
