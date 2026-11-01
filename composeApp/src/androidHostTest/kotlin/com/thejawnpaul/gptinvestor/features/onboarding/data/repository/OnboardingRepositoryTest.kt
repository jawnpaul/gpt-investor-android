package com.thejawnpaul.gptinvestor.features.onboarding.data.repository

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.OnboardingSuggestionsRemote
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.SuggestedStockRemote
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSuggestions
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.SuggestedStock
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class OnboardingRepositoryTest {

    private val apiService: KtorApiService = mockk()
    private val repository = OnboardingRepository(apiService)

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

    private fun stubResponse(body: OnboardingSuggestionsRemote?, isSuccessful: Boolean = true, code: Int = 200) {
        coEvery { apiService.getOnboardingSuggestions() } returns KtorResponse(
            isSuccessful = isSuccessful,
            body = body,
            errorBody = null,
            code = code
        )
    }

    @Test
    fun `should return the list with https logos when the server answers`() = runTest {
        stubResponse(remote)

        val result = repository.fetchSuggestions()

        assertThat(result).isEqualTo(Either.Right(expected))
    }

    @Test
    fun `should fail when the list is empty`() = runTest {
        stubResponse(OnboardingSuggestionsRemote(market = "US", stocks = emptyList()))

        val result = repository.fetchSuggestions()

        assertThat(result).isEqualTo(Either.Left(Failure.ServerError))
    }

    @Test
    fun `should fail when the endpoint is missing`() = runTest {
        stubResponse(body = null, isSuccessful = false, code = 404)

        val result = repository.fetchSuggestions()

        assertThat(result).isEqualTo(Either.Left(Failure.ServerError))
    }

    @Test
    fun `should report a connection failure when the request throws`() = runTest {
        coEvery { apiService.getOnboardingSuggestions() } throws RuntimeException("offline")

        val result = repository.fetchSuggestions()

        assertThat(result).isEqualTo(Either.Left(Failure.NetworkConnection))
    }
}
