package com.thejawnpaul.gptinvestor.features.watchlist.domain

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model.AddWatchlistResponse
import com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model.RemoveWatchlistResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WatchlistRepositoryImplTest {

    private val apiService: KtorApiService = mockk()
    private val repository = WatchlistRepositoryImpl(apiService)

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
}
