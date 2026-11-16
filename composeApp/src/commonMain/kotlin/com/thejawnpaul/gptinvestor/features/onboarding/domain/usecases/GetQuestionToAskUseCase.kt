package com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.onboarding.domain.AskSession
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.AskTrigger
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestionFailure
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingQuestionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class GetQuestionToAskUseCase(
    @Provided @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    @Provided coroutineScope: CoroutineScope,
    private val repository: IOnboardingQuestionRepository,
    private val askSession: AskSession
) : BaseUseCase<GetQuestionToAskUseCase.Params, OnboardingQuestion>(coroutineScope, dispatcher) {

    data class Params(val trigger: AskTrigger, val isGuest: Boolean)

    override suspend fun run(params: Params): Flow<Either<Failure, OnboardingQuestion>> = flow {
        val progress = when (val result = repository.getProgress()) {
            is Either.Left -> {
                emit(result)
                return@flow
            }
            is Either.Right -> result.b
        }
        val question = OnboardingQuestion.entries
            .filter { it.trigger == params.trigger && (it.askGuests || !params.isGuest) }
            .firstOrNull { !progress.stateOf(it).isSettled }
        if (question != null && askSession.claim(progress.owner)) {
            emit(Either.Right(question))
        } else {
            emit(Either.Left(OnboardingQuestionFailure.NothingToAsk))
        }
    }
}
