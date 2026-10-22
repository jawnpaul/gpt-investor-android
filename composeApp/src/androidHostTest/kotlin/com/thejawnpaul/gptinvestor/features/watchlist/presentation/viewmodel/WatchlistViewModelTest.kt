package com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumRequest
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistFailure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistData
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistItem
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.WatchlistLoadError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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
    private val companyRepository: ICompanyRepository = mockk()
    private val premiumEventBus = PremiumEventBus()
    private val changes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val purchaseVerified = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val billingRepository: IBillingRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { watchlistRepository.changes } returns changes
        every { billingRepository.purchaseVerified } returns purchaseVerified
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.subscribedViewModel(): WatchlistViewModel {
        val vm =
            WatchlistViewModel(
                watchlistRepository,
                companyRepository,
                premiumEventBus,
                billingRepository,
                analyticsLogger
            )
        backgroundScope.launch { vm.uiState.collect { } }
        advanceUntilIdle()
        return vm
    }

    @Test
    fun `does not load until the screen subscribes`() = runTest(testDispatcher) {
        WatchlistViewModel(watchlistRepository, companyRepository, premiumEventBus, billingRepository, analyticsLogger)
        advanceUntilIdle()

        coVerify(exactly = 0) { watchlistRepository.getWatchlist() }
    }

    @Test
    fun `reloads when a stock is added or removed elsewhere`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returnsMany listOf(
            loaded("AAPL"),
            loaded("AAPL", "TSLA")
        )
        val vm = subscribedViewModel()
        assertThat(vm.uiState.value.watchlistItems.map { it.ticker }).containsExactly("AAPL")

        changes.emit(Unit)
        advanceUntilIdle()

        assertThat(vm.uiState.value.watchlistItems.map { it.ticker }).containsExactly("AAPL", "TSLA").inOrder()
    }

    @Test
    fun `reloads after a purchase is verified so locked items unlock`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returnsMany listOf(loaded("AAPL"), loaded("AAPL", "TSLA"))
        val vm = subscribedViewModel()

        purchaseVerified.emit(Unit)
        advanceUntilIdle()

        coVerify(exactly = 2) { watchlistRepository.getWatchlist() }
        assertThat(vm.uiState.value.watchlistItems.map { it.ticker }).containsExactly("AAPL", "TSLA").inOrder()
    }

    @Test
    fun `retry reloads after a failure`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returnsMany listOf(
            Either.Left(Failure.ServerError),
            loaded("AAPL")
        )
        val vm = subscribedViewModel()
        assertThat(vm.uiState.value.error).isNotNull()

        vm.onEvent(WatchlistEvent.RetryWatchlist)
        advanceUntilIdle()

        assertThat(vm.uiState.value.error).isNull()
        assertThat(vm.uiState.value.watchlistItems.map { it.ticker }).containsExactly("AAPL")
    }

    @Test
    fun `guest 403 shows sign up prompt instead of an error`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returns Either.Left(WatchlistFailure.SignUpRequired)

        val vm = subscribedViewModel()

        assertThat(vm.uiState.value.requiresSignUp).isTrue()
        assertThat(vm.uiState.value.error).isNull()
    }

    @Test
    fun `server failure shows an error, not the sign up prompt`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returns Either.Left(Failure.ServerError)

        val vm = subscribedViewModel()

        assertThat(vm.uiState.value.requiresSignUp).isFalse()
        assertThat(vm.uiState.value.error).isNotNull()
    }

    @Test
    fun `OnSignInClick emits NavigateToLogin`() = runTest(testDispatcher) {
        val vm =
            WatchlistViewModel(
                watchlistRepository,
                companyRepository,
                premiumEventBus,
                billingRepository,
                analyticsLogger
            )

        vm.actions.test {
            vm.onEvent(WatchlistEvent.OnSignInClick)
            assertThat(awaitItem()).isEqualTo(WatchlistAction.NavigateToLogin)
        }
    }

    @Test
    fun `OnCreateAccountClick emits NavigateToSignUp`() = runTest(testDispatcher) {
        val vm =
            WatchlistViewModel(
                watchlistRepository,
                companyRepository,
                premiumEventBus,
                billingRepository,
                analyticsLogger
            )

        vm.actions.test {
            vm.onEvent(WatchlistEvent.OnCreateAccountClick)
            assertThat(awaitItem()).isEqualTo(WatchlistAction.NavigateToSignUp)
        }
    }

    @Test
    fun `network and server failures show different errors`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returnsMany listOf(
            Either.Left(Failure.NetworkConnection),
            Either.Left(Failure.ServerError)
        )
        val vm = subscribedViewModel()
        assertThat(vm.uiState.value.error).isEqualTo(WatchlistLoadError.NETWORK)

        vm.onEvent(WatchlistEvent.RetryWatchlist)
        advanceUntilIdle()

        assertThat(vm.uiState.value.error).isEqualTo(WatchlistLoadError.SERVER)
    }

    @Test
    fun `empty watchlist shows popular stocks with fetched logos`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returns Either.Right(
            WatchlistData(
                items = emptyList(),
                recommendations = listOf(
                    RecommendedDigestCompany("AAPL", "Apple Inc."),
                    RecommendedDigestCompany("TSLA", "Tesla")
                )
            )
        )
        coEvery { companyRepository.getCompanyLogos(listOf("AAPL", "TSLA")) } returns
            Either.Right(mapOf("AAPL" to "https://logo/aapl.png"))

        val vm = subscribedViewModel()

        val logos = vm.uiState.value.popularStocks.associate { it.company.ticker to it.logoUrl }
        assertThat(logos).containsExactly("AAPL", "https://logo/aapl.png", "TSLA", null)
    }

    private fun loaded(vararg tickers: String): Either<Failure, WatchlistData> = Either.Right(
        WatchlistData(
            items = tickers.map {
                WatchlistItem(ticker = it, companyName = it, dateAdded = "2026-10-10", locked = false, logoUrl = "")
            },
            recommendations = emptyList()
        )
    )

    @Test
    fun `adding to a full watchlist opens the paywall with the cap`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.getWatchlist() } returns
            Either.Right(WatchlistData(items = emptyList(), recommendations = emptyList()))
        coEvery { watchlistRepository.addStockToWatchList("AAPL") } returns
            Either.Left(WatchlistFailure.WatchlistFull(cap = 2))
        val vm = subscribedViewModel()

        premiumEventBus.events.test {
            vm.onEvent(WatchlistEvent.OnWatchClick("AAPL"))
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(
                PremiumRequest(source = "watchlist_full", watchlistCap = 2, ticker = "AAPL")
            )
        }
    }
}
