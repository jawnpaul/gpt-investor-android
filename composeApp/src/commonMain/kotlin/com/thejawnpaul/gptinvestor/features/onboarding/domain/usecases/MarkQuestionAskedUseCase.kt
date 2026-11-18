package com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingQuestionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class MarkQuestionAskedUseCase(
    @Provided @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    @Provided coroutineScope: CoroutineScope,
    private val repository: IOnboardingQuestionRepository
) : BaseUseCase<OnboardingQuestion, Unit>(coroutineScope, dispatcher) {

    override suspend fun run(params: OnboardingQuestion): Flow<Either<Failure, Unit>> = flow {
        emit(repository.markAsked(params))
    }
}
