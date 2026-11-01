package com.thejawnpaul.gptinvestor.features.onboarding.data.repository

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.OnboardingSuggestionsRemote
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.SuggestedStockRemote
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSuggestions
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.SuggestedStock
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class OnboardingRepositoryTest {

    private val apiService: KtorApiService = mockk()
    private val appPreferences: AppPreferences = mockk()
    private val repository = OnboardingRepository(apiService, appPreferences)

    private val remote = OnboardingSuggestionsRemote(
        market = "NG",
        stocks = listOf(
            SuggestedStockRemote("MTNN.NG", "MTN Nigeria", "http://logo/mtn.png"),
            SuggestedStockRemote("AAPL", "Apple", null)
        )
    )

    private val expected = OnboardingSuggestions(
        market = "NG",
        stocks = listOf(
            SuggestedStock("MTNN.NG", "MTN Nigeria", "https://logo/mtn.png"),
            SuggestedStock("AAPL", "Apple", "")
        )
    )

    @Before
    fun setUp() {
        coJustRun { appPreferences.setOnboardingSuggestions(any()) }
    }

    private fun stubResponse(body: OnboardingSuggestionsRemote?, isSuccessful: Boolean = true, code: Int = 200) {
        coEvery { apiService.getOnboardingSuggestions() } returns KtorResponse(
            isSuccessful = isSuccessful,
            body = body,
            errorBody = null,
            code = code
        )
    }

    @Test
    fun `should return the list and save it when the server answers`() = runTest {
        val savedJson = slot<String>()
        coJustRun { appPreferences.setOnboardingSuggestions(capture(savedJson)) }
        stubResponse(remote)

        val result = repository.fetchSuggestions()

        assertThat(result).isEqualTo(Either.Right(expected))
        every { appPreferences.onboardingSuggestions } returns flowOf(savedJson.captured)
        assertThat(repository.getSavedSuggestions()).isEqualTo(expected)
    }

    @Test
    fun `should fail without saving when the list is empty`() = runTest {
        stubResponse(OnboardingSuggestionsRemote(market = "US", stocks = emptyList()))

        val result = repository.fetchSuggestions()

        assertThat(result).isEqualTo(Either.Left(Failure.ServerError))
        coVerify(exactly = 0) { appPreferences.setOnboardingSuggestions(any()) }
    }

    @Test
    fun `should fail without saving when the endpoint is missing`() = runTest {
        stubResponse(body = null, isSuccessful = false, code = 404)

        val result = repository.fetchSuggestions()

        assertThat(result).isEqualTo(Either.Left(Failure.ServerError))
        coVerify(exactly = 0) { appPreferences.setOnboardingSuggestions(any()) }
    }

    @Test
    fun `should report a connection failure when the request throws`() = runTest {
        coEvery { apiService.getOnboardingSuggestions() } throws RuntimeException("offline")

        val result = repository.fetchSuggestions()

        assertThat(result).isEqualTo(Either.Left(Failure.NetworkConnection))
    }

    @Test
    fun `should return null when nothing is saved`() = runTest {
        every { appPreferences.onboardingSuggestions } returns flowOf(null)

        assertThat(repository.getSavedSuggestions()).isNull()
    }

    @Test
    fun `should return null when the saved list is unreadable`() = runTest {
        every { appPreferences.onboardingSuggestions } returns flowOf("not json")

        assertThat(repository.getSavedSuggestions()).isNull()
    }
}
