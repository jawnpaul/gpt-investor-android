package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.session.GuestSessionNotifier
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyBriefUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class CompanyViewModelWatchlistTest {

    private val testDispatcher = StandardTestDispatcher()
    private val watchlistRepository: WatchlistRepository = mockk()
    private val modelsRepository: ModelsRepository = mockk()
    private val appPreferences: AppPreferences = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { modelsRepository.getAvailableModels() } returns Result.success(emptyList())
        every { appPreferences.isGuestLoggedIn } returns flowOf(false)
        every { appPreferences.hasBriefSpotlightSeen } returns flowOf(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.subscribedViewModel(): CompanyViewModel {
        val vm = viewModel()
        backgroundScope.launch { vm.selectedCompany.collect { } }
        advanceUntilIdle()
        return vm
    }

    private fun viewModel() = CompanyViewModel(
        getCompanyBriefUseCase = mockk<GetCompanyBriefUseCase>(relaxed = true),
        getCompanyUseCase = mockk<GetCompanyUseCase>(relaxed = true),
        savedStateHandle = SavedStateHandle(mapOf("ticker" to "AAPL")),
        getInputPromptUseCase = mockk<GetInputPromptUseCase>(relaxed = true),
        modelsRepository = modelsRepository,
        feedbackRepository = mockk<FeedbackRepository>(relaxed = true),
        appPreferences = appPreferences,
        watchlistRepository = watchlistRepository,
        premiumEventBus = PremiumEventBus(),
        analyticsLogger = mockk<AnalyticsLogger>(relaxed = true),
        guestSessionNotifier = mockk<GuestSessionNotifier>(relaxed = true)
    )

    @Test
    fun `adding to the watchlist is pending until the call returns`() = runTest(testDispatcher) {
        val response = CompletableDeferred<Either<Failure, Unit>>()
        coEvery { watchlistRepository.addStockToWatchList("AAPL") } coAnswers { response.await() }
        val vm = subscribedViewModel()

        vm.handleCompanyDetailEvent(CompanyDetailEvent.AddToWatchlist)
        advanceUntilIdle()
        assertThat(vm.selectedCompany.value.watchlistPending).isTrue()

        response.complete(Either.Right(Unit))
        advanceUntilIdle()
        assertThat(vm.selectedCompany.value.watchlistPending).isFalse()
    }

    @Test
    fun `a second tap while adding does not send another request`() = runTest(testDispatcher) {
        val response = CompletableDeferred<Either<Failure, Unit>>()
        coEvery { watchlistRepository.addStockToWatchList("AAPL") } coAnswers { response.await() }
        val vm = subscribedViewModel()

        vm.handleCompanyDetailEvent(CompanyDetailEvent.AddToWatchlist)
        vm.handleCompanyDetailEvent(CompanyDetailEvent.AddToWatchlist)
        response.complete(Either.Right(Unit))
        advanceUntilIdle()

        coVerify(exactly = 1) { watchlistRepository.addStockToWatchList("AAPL") }
    }

    @Test
    fun `a guest tap asks to sign in and sends no request`() = runTest(testDispatcher) {
        every { appPreferences.isGuestLoggedIn } returns flowOf(true)
        val vm = subscribedViewModel()

        vm.companyDetailAction.test {
            vm.handleCompanyDetailEvent(CompanyDetailEvent.AddToWatchlist)
            assertThat(awaitItem()).isEqualTo(CompanyDetailAction.WatchlistSignInRequired)
        }
        coVerify(exactly = 0) { watchlistRepository.addStockToWatchList(any()) }
    }

    @Test
    fun `a failed add clears pending`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.addStockToWatchList("AAPL") } returns Either.Left(Failure.NetworkConnection)
        val vm = subscribedViewModel()

        vm.handleCompanyDetailEvent(CompanyDetailEvent.AddToWatchlist)
        advanceUntilIdle()

        assertThat(vm.selectedCompany.value.watchlistPending).isFalse()
    }
}
