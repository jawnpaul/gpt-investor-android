package com.thejawnpaul.gptinvestor.features.onboarding.data.repository

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.platform.InstallationIdProvider
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.MarkQuestionAskedRequest
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.OnboardingSyncResponse
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.SaveOnboardingAnswersResponse
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingProgress
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSync
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.QuestionState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Before
import org.junit.Test

class OnboardingQuestionRepositoryImplTest {

    private val apiService: KtorApiService = mockk()
    private val preferences: AppPreferences = mockk()
    private val installationIdProvider = object : InstallationIdProvider {
        override suspend fun get() = INSTALLATION_ID
    }
    private val repository = OnboardingQuestionRepositoryImpl(apiService, preferences, installationIdProvider)
    private val ok =
        KtorResponse(isSuccessful = true, body = SaveOnboardingAnswersResponse("ok"), errorBody = null, code = 200)

    @Before
    fun setUp() {
        every { preferences.hasCompletedPostAuthOnboarding } returns flowOf(null)
        every { preferences.isUserLoggedIn } returns flowOf(true)
        every { preferences.isGuestLoggedIn } returns flowOf(false)
        every { preferences.userId } returns flowOf(USER_ID)
        coEvery { preferences.setHasCompletedPostAuthOnboarding(any()) } returns Unit
        coEvery { apiService.saveOnboardingAnswers(any(), any()) } returns ok
        coEvery { apiService.markOnboardingQuestionAsked(any(), any()) } returns ok
    }

    private fun serverReturns(response: OnboardingSyncResponse) {
        coEvery { apiService.syncOnboarding(INSTALLATION_ID) } returns
            KtorResponse(isSuccessful = true, body = response, errorBody = null, code = 200)
    }

    @Test
    fun `sync keeps the server's answers and asked questions and drops unset answers`() = runTest {
        serverReturns(
            OnboardingSyncResponse(
                answers = mapOf("experience" to "BEGINNER", "goal" to null),
                asked = listOf("experience", "alerts"),
                addedStock = "KO"
            )
        )

        val result = repository.sync()

        assertThat(result).isEqualTo(
            Either.Right(
                OnboardingSync(
                    progress = OnboardingProgress(
                        owner = USER_ID,
                        answers = mapOf("experience" to "BEGINNER"),
                        asked = setOf("experience", "alerts")
                    ),
                    addedStock = "KO"
                )
            )
        )
        assertThat(repository.getQuestionState(OnboardingQuestion.EXPERIENCE).first())
            .isEqualTo(QuestionState(OnboardingQuestion.EXPERIENCE, answer = "BEGINNER", isAsked = true))
    }

    @Test
    fun `progress is fetched once and then served from memory`() = runTest {
        serverReturns(OnboardingSyncResponse())

        repository.getProgress()
        repository.getProgress()

        coVerify(exactly = 1) { apiService.syncOnboarding(any()) }
    }

    @Test
    fun `nothing is known before the first sync`() = runTest {
        assertThat(repository.getQuestionState(OnboardingQuestion.GOAL).first()).isNull()
    }

    @Test
    fun `saving an answer sends only that question and updates what screens see`() = runTest {
        serverReturns(OnboardingSyncResponse())
        repository.sync()

        val result = repository.saveAnswer(OnboardingQuestion.GOAL, "GROW_WEALTH")

        assertThat(result).isEqualTo(Either.Right(Unit))
        coVerify {
            apiService.saveOnboardingAnswers(INSTALLATION_ID, buildJsonObject { put("goal", "GROW_WEALTH") })
        }
        assertThat(repository.getQuestionState(OnboardingQuestion.GOAL).first())
            .isEqualTo(QuestionState(OnboardingQuestion.GOAL, answer = "GROW_WEALTH", isAsked = true))
    }

    @Test
    fun `marking a question asked tells the server`() = runTest {
        repository.markAsked(OnboardingQuestion.ALERTS)

        coVerify { apiService.markOnboardingQuestionAsked(INSTALLATION_ID, MarkQuestionAskedRequest("alerts")) }
    }

    @Test
    fun `accounts that finished the old screen are not asked experience again`() = runTest {
        every { preferences.hasCompletedPostAuthOnboarding } returns flowOf(true)
        serverReturns(OnboardingSyncResponse())

        repository.sync()

        coVerify { apiService.markOnboardingQuestionAsked(INSTALLATION_ID, MarkQuestionAskedRequest("experience")) }
        assertThat(repository.getQuestionState(OnboardingQuestion.EXPERIENCE).first()?.isSettled).isTrue()
        coVerify { preferences.setHasCompletedPostAuthOnboarding(false) }
    }

    @Test
    fun `progress cached for a guest is fetched again once they sign up`() = runTest {
        serverReturns(OnboardingSyncResponse())
        every { preferences.isGuestLoggedIn } returns flowOf(true)
        repository.getProgress()

        every { preferences.isGuestLoggedIn } returns flowOf(false)
        val progress = repository.getProgress()

        coVerify(exactly = 2) { apiService.syncOnboarding(any()) }
        assertThat((progress as Either.Right).b.owner).isEqualTo(USER_ID)
    }

    @Test
    fun `an offline sync is a network failure`() = runTest {
        coEvery { apiService.syncOnboarding(any()) } throws RuntimeException("offline")

        assertThat(repository.sync()).isEqualTo(Either.Left(Failure.NetworkConnection))
    }

    private companion object {
        const val INSTALLATION_ID = "install-1"
        const val USER_ID = "user-1"
    }
}
