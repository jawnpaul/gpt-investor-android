package com.thejawnpaul.gptinvestor.features.watchlist.domain

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model.AddWatchlistResponse
import com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model.RemoveWatchlistResponse
import com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model.WatchlistResponse
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WatchlistRepositoryImplTest {

    private val apiService: KtorApiService = mockk()
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val repository = WatchlistRepositoryImpl(apiService, analyticsLogger)

    @Test
    fun `successful add emits a change`() = runTest {
        coEvery { apiService.addToWatchlist(any()) } returns
            KtorResponse(
                isSuccessful = true,
                body = AddWatchlistResponse(ticker = "AAPL"),
                errorBody = null,
                code = 201
            )

        repository.changes.test {
            repository.addStockToWatchList("AAPL")
            assertThat(awaitItem()).isEqualTo(Unit)
        }
    }

    @Test
    fun `successful remove emits a change`() = runTest {
        coEvery { apiService.removeFromWatchlist("AAPL") } returns
            KtorResponse(
                isSuccessful = true,
                body = RemoveWatchlistResponse(success = true),
                errorBody = null,
                code = 200
            )

        repository.changes.test {
            repository.removeFromWatchlist("AAPL")
            assertThat(awaitItem()).isEqualTo(Unit)
        }
    }

    @Test
    fun `failed add does not emit a change`() = runTest {
        coEvery { apiService.addToWatchlist(any()) } returns
            KtorResponse(
                isSuccessful = false,
                body = null,
                errorBody = """{"error":"watchlist_full","cap":3}""",
                code = 409
            )

        repository.changes.test {
            repository.addStockToWatchList("AAPL")
            expectNoEvents()
        }
    }

    @Test
    fun `free plan watchlist sets free_watchlist_limit from cap`() = runTest {
        coEvery { apiService.getWatchlist() } returns
            KtorResponse(
                isSuccessful = true,
                body = WatchlistResponse(cap = 2, plan = "free"),
                errorBody = null,
                code = 200
            )

        repository.getWatchlist()

        verify { analyticsLogger.setUserProperty("free_watchlist_limit", "2") }
    }

    @Test
    fun `premium plan watchlist does not touch free_watchlist_limit`() = runTest {
        coEvery { apiService.getWatchlist() } returns
            KtorResponse(
                isSuccessful = true,
                body = WatchlistResponse(cap = 50, plan = "premium"),
                errorBody = null,
                code = 200
            )

        repository.getWatchlist()

        verify(exactly = 0) { analyticsLogger.setUserProperty(any(), any()) }
    }

    @Test
    fun `watchlist full logs event with cap and sets user property`() = runTest {
        coEvery { apiService.addToWatchlist(any()) } returns
            KtorResponse(
                isSuccessful = false,
                body = null,
                errorBody = """{"error":"watchlist_full","cap":3}""",
                code = 409
            )

        val result = repository.addStockToWatchList("AAPL")

        assertThat(result.isLeft).isTrue()
        verify(exactly = 1) { analyticsLogger.logEvent("watchlist_full", mapOf("cap" to 3)) }
        verify { analyticsLogger.setUserProperty("free_watchlist_limit", "3") }
    }
}
