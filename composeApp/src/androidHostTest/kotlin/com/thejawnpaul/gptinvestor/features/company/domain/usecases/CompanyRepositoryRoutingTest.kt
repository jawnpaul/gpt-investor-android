package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CompanyRepositoryRoutingTest {

    @MockK
    lateinit var apiService: KtorApiService

    @MockK
    lateinit var companyDao: CompanyDao

    @MockK
    lateinit var analyticsLogger: AnalyticsLogger

    @MockK
    lateinit var appPreferences: AppPreferences

    private lateinit var repository: CompanyRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        justRun { analyticsLogger.logEvent(any(), any()) }
        repository = CompanyRepository(apiService, companyDao, analyticsLogger, appPreferences)
    }

    private fun stubGuest(isGuest: Boolean) {
        every { appPreferences.isGuestLoggedIn } returns flowOf(isGuest)
    }

    private fun briefResponse(code: Int, errorBody: String?) =
        KtorResponse<com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyBriefRemote>(
            isSuccessful = false,
            body = null,
            errorBody = errorBody,
            code = code
        )

    @Test
    fun `guest_session_expired code returns GuestSessionExpired failure`() = runTest {
        stubGuest(isGuest = true)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(
            401,
            """{"code":"guest_session_expired","message":"Session expired"}"""
        )

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat(result).isInstanceOf(Either.Left::class.java)
            assertThat((result as Either.Left).a).isEqualTo(Failure.GuestSessionExpired)
            awaitComplete()
        }
    }

    @Test
    fun `guest_limit_reached code returns GuestLimitReached failure`() = runTest {
        stubGuest(isGuest = true)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(
            429,
            """{"code":"guest_limit_reached","message":"Limit reached","limit":5}"""
        )

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.GuestLimitReached)
            awaitComplete()
        }
    }

    @Test
    fun `rate_limited code returns RateLimitExceeded failure`() = runTest {
        stubGuest(isGuest = false)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(
            429,
            """{"code":"rate_limited","message":"Too many requests"}"""
        )

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.RateLimitExceeded)
            awaitComplete()
        }
    }

    @Test
    fun `plain 500 returns ServerError failure`() = runTest {
        stubGuest(isGuest = false)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(
            500,
            """{"message":"Internal server error"}"""
        )

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.ServerError)
            awaitComplete()
        }
    }

    @Test
    fun `network exception returns NetworkConnection failure`() = runTest {
        stubGuest(isGuest = false)
        coEvery { apiService.getCompanyBrief("AAPL") } throws Exception("Network error")

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.NetworkConnection)
            awaitComplete()
        }
    }

    @Test
    fun `no-code 401 for guest returns GuestSessionExpired`() = runTest {
        stubGuest(isGuest = true)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(401, null)

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.GuestSessionExpired)
            awaitComplete()
        }
    }

    @Test
    fun `no-code 401 for registered user returns ServerError`() = runTest {
        stubGuest(isGuest = false)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(401, null)

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.ServerError)
            awaitComplete()
        }
    }

    @Test
    fun `no-code 429 returns RateLimitExceeded failure`() = runTest {
        stubGuest(isGuest = false)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(429, null)

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.RateLimitExceeded)
            awaitComplete()
        }
    }
}
