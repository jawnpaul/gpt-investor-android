package com.thejawnpaul.gptinvestor.features.onboarding.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingProgress
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSync
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.QuestionState
import kotlinx.coroutines.flow.Flow

interface IOnboardingQuestionRepository {
    fun getQuestionState(question: OnboardingQuestion): Flow<QuestionState?>

    suspend fun getProgress(): Either<Failure, OnboardingProgress>

    suspend fun sync(): Either<Failure, OnboardingSync>

    suspend fun markAsked(question: OnboardingQuestion): Either<Failure, Unit>

    suspend fun saveAnswer(question: OnboardingQuestion, answer: String): Either<Failure, Unit>
}
