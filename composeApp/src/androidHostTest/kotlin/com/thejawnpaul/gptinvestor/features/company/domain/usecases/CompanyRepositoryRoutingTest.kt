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
import io.mockk.verify
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

    private fun stubGuest(isGuest: Boolean, isRegistered: Boolean = !isGuest) {
        every { appPreferences.isGuestLoggedIn } returns flowOf(isGuest)
        every { appPreferences.isUserLoggedIn } returns flowOf(isRegistered)
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
            """{"error":"guest_session_expired","message":"Session expired"}"""
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
            """{"error":"guest_limit_reached","limit":3,"upgrade":true}"""
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
            """{"error":"rate_limited","tier":"normal","limit":"2 per 1 day","retry_after":43210,"upgrade":true}"""
        )

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a)
                .isEqualTo(Failure.RateLimitExceeded(upgrade = true, retryAfterSeconds = 43210))
            awaitComplete()
        }
    }

    @Test
    fun `premium rate_limited returns RateLimitExceeded without upgrade`() = runTest {
        stubGuest(isGuest = false)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(
            429,
            """{"error":"rate_limited","tier":"premium","limit":"10 per 1 minute","retry_after":42,"upgrade":false}"""
        )

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a)
                .isEqualTo(Failure.RateLimitExceeded(upgrade = false, retryAfterSeconds = 42))
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
    fun `401 during onboarding returns GuestSessionExpired`() = runTest {
        stubGuest(isGuest = false, isRegistered = false)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(
            401,
            """{"error":"token_expired","msg":"Token has expired"}"""
        )

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.GuestSessionExpired)
            awaitComplete()
        }
    }

    @Test
    fun `no-code 429 returns RateLimitExceeded failure`() = runTest {
        stubGuest(isGuest = false)
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(429, null)

        repository.getCompanyBrief("AAPL").test {
            val result = awaitItem()
            assertThat((result as Either.Left).a).isEqualTo(Failure.RateLimitExceeded())
            awaitComplete()
        }
    }

    @Test
    fun `brief events label the session as authenticated, guest or onboarding`() = runTest {
        coEvery { apiService.getCompanyBrief("AAPL") } returns briefResponse(500, null)

        listOf(
            SessionCase(isGuest = false, isRegistered = true, userType = "authenticated"),
            SessionCase(isGuest = true, isRegistered = false, userType = "guest"),
            SessionCase(isGuest = false, isRegistered = false, userType = "onboarding")
        ).forEach { (isGuest, isRegistered, userType) ->
            stubGuest(isGuest = isGuest, isRegistered = isRegistered)
            repository.getCompanyBrief("AAPL").test {
                awaitItem()
                awaitComplete()
            }
            verify {
                analyticsLogger.logEvent("brief-started", mapOf("ticker" to "AAPL", "user_type" to userType))
            }
        }
    }
}

private data class SessionCase(val isGuest: Boolean, val isRegistered: Boolean, val userType: String)
