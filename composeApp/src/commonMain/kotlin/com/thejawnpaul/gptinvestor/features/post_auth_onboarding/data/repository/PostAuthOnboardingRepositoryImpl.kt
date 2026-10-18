package com.thejawnpaul.gptinvestor.features.postauthonboarding.data.repository

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.postauthonboarding.data.remote.SaveOnboardingAnswersRequest
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.RiskTolerance
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.repository.IPostAuthOnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Singleton

@Singleton(binds = [IPostAuthOnboardingRepository::class])
class PostAuthOnboardingRepositoryImpl(private val apiService: KtorApiService) : IPostAuthOnboardingRepository {

    override fun saveOnboardingAnswers(
        experience: InvestingExperience?,
        goal: InvestingGoal?,
        riskTolerance: RiskTolerance?
    ): Flow<Either<Failure, Unit>> = flow {
        try {
            val request = SaveOnboardingAnswersRequest(
                experience = experience?.name,
                goal = goal?.name,
                riskTolerance = riskTolerance?.name
            )
            val response = apiService.saveOnboardingAnswers(request)
            if (response.isSuccessful) {
                emit(Either.Right(Unit))
            } else {
                emit(Either.Left(Failure.ServerError))
            }
        } catch (e: Exception) {
            emit(Either.Left(Failure.NetworkConnection))
        }
    }
}
