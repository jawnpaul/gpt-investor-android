package com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.onboarding.domain.AskSession
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.AskTrigger
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingProgress
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestionFailure
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingQuestionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetQuestionToAskUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository: IOnboardingQuestionRepository = mockk()
    private val askSession = AskSession()
    private val useCase = GetQuestionToAskUseCase(dispatcher, TestScope(dispatcher), repository, askSession)

    private fun progress(
        answers: Map<String, String> = emptyMap(),
        asked: Set<String> = emptySet(),
        owner: String = "user-1"
    ) {
        coEvery { repository.getProgress() } returns Either.Right(OnboardingProgress(owner, answers, asked))
    }

    private suspend fun ask(trigger: AskTrigger, isGuest: Boolean = false) =
        useCase.run(GetQuestionToAskUseCase.Params(trigger, isGuest)).first()

    @Test
    fun `asks the question for the trigger when it hasn't been asked`() = runTest {
        progress()

        assertThat(ask(AskTrigger.HOME)).isEqualTo(Either.Right(OnboardingQuestion.EXPERIENCE))
    }

    @Test
    fun `asks at most one question per session`() = runTest {
        progress()

        assertThat(ask(AskTrigger.HOME)).isEqualTo(Either.Right(OnboardingQuestion.EXPERIENCE))
        assertThat(ask(AskTrigger.DISCOVER)).isEqualTo(Either.Left(OnboardingQuestionFailure.NothingToAsk))
    }

    @Test
    fun `does not ask a question that was asked or answered`() = runTest {
        progress(answers = mapOf("goal" to "GROW_WEALTH"), asked = setOf("experience"))

        assertThat(ask(AskTrigger.HOME)).isEqualTo(Either.Left(OnboardingQuestionFailure.NothingToAsk))
        assertThat(ask(AskTrigger.DISCOVER)).isEqualTo(Either.Left(OnboardingQuestionFailure.NothingToAsk))
    }

    @Test
    fun `guests are asked only the questions meant for guests`() = runTest {
        progress()

        assertThat(ask(AskTrigger.HOME, isGuest = true)).isEqualTo(Either.Left(OnboardingQuestionFailure.NothingToAsk))
        assertThat(ask(AskTrigger.DISCOVER, isGuest = true)).isEqualTo(Either.Right(OnboardingQuestion.GOAL))
    }

    @Test
    fun `a failed load asks nothing and leaves the session free`() = runTest {
        coEvery { repository.getProgress() } returns Either.Left(Failure.NetworkConnection)

        assertThat(ask(AskTrigger.HOME)).isEqualTo(Either.Left(Failure.NetworkConnection))
        assertThat(askSession.claim("user-1")).isTrue()
    }

    @Test
    fun `someone new on the same phone gets their own question this session`() = runTest {
        progress(owner = "guest")
        assertThat(ask(AskTrigger.DISCOVER, isGuest = true)).isEqualTo(Either.Right(OnboardingQuestion.GOAL))

        progress(owner = "user-1")

        assertThat(ask(AskTrigger.HOME)).isEqualTo(Either.Right(OnboardingQuestion.EXPERIENCE))
    }
}
