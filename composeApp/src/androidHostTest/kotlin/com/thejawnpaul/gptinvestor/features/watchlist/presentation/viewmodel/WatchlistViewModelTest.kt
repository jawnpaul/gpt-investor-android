package com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistFailure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val watchlistRepository: WatchlistRepository = mockk()
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guest 403 shows sign up prompt instead of an error`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returns Either.Left(WatchlistFailure.SignUpRequired)

        val vm = WatchlistViewModel(watchlistRepository, analyticsLogger)
        advanceUntilIdle()

        assertThat(vm.uiState.value.requiresSignUp).isTrue()
        assertThat(vm.uiState.value.error).isNull()
    }

    @Test
    fun `server failure shows an error, not the sign up prompt`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returns Either.Left(Failure.ServerError)

        val vm = WatchlistViewModel(watchlistRepository, analyticsLogger)
        advanceUntilIdle()

        assertThat(vm.uiState.value.requiresSignUp).isFalse()
        assertThat(vm.uiState.value.error).isNotNull()
    }

    @Test
    fun `OnSignInClick emits NavigateToSignUp`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returns Either.Left(WatchlistFailure.SignUpRequired)
        val vm = WatchlistViewModel(watchlistRepository, analyticsLogger)

        vm.actions.test {
            vm.onEvent(WatchlistEvent.OnSignInClick)
            assertThat(awaitItem()).isEqualTo(WatchlistAction.NavigateToSignUp)
        }
    }
}
