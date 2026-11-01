package com.thejawnpaul.gptinvestor.features.onboarding.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyBriefUseCase
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSuggestions
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.SuggestedStock
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelSuggestionsTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getCompanyBriefUseCase: GetCompanyBriefUseCase = mockk()
    private val companyRepository: CompanyRepository = mockk()
    private val appPreferences: AppPreferences = mockk(relaxed = true)
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val authRepository: AuthenticationRepository = mockk(relaxed = true)
    private val onboardingRepository: IOnboardingRepository = mockk()

    private val nigeria = OnboardingSuggestions(
        market = "NG",
        stocks = listOf(
            SuggestedStock("MTNN.NG", "MTN Nigeria", "https://logo/mtn.png"),
            SuggestedStock("AAPL", "Apple", "https://logo/aapl.png")
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { appPreferences.isUserLoggedIn } returns flowOf(false)
        coEvery { authRepository.acquireGuestToken() } returns Result.success(Unit)
        coEvery { companyRepository.getCompanyLogos(any()) } returns Either.Right(emptyMap())
        every { getCompanyBriefUseCase(any(), any()) } returns Job()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = OnboardingViewModel(
        getCompanyBriefUseCase = getCompanyBriefUseCase,
        companyRepository = companyRepository,
        appPreferences = appPreferences,
        authRepository = authRepository,
        onboardingRepository = onboardingRepository,
        analyticsLogger = analyticsLogger
    )

    private fun OnboardingViewModel.openPicker() {
        onNextScreen()
        onNextScreen()
    }

    @Test
    fun `should show the server list when it arrives`() = runTest(testDispatcher) {
        coEvery { onboardingRepository.fetchSuggestions() } returns Either.Right(nigeria)
        val vm = viewModel()

        advanceUntilIdle()

        assertThat(vm.uiState.value.suggestedStocks).isEqualTo(nigeria.stocks)
    }

    @Test
    fun `should keep the server list when built-in logos arrive later`() = runTest(testDispatcher) {
        val logos = CompletableDeferred<Either<Failure, Map<String, String>>>()
        coEvery { companyRepository.getCompanyLogos(any()) } coAnswers { logos.await() }
        coEvery { onboardingRepository.fetchSuggestions() } returns Either.Right(nigeria)
        val vm = viewModel()
        advanceUntilIdle()

        logos.complete(Either.Right(mapOf("AAPL" to "https://logo/old-aapl.png")))
        advanceUntilIdle()

        assertThat(vm.uiState.value.suggestedStocks).isEqualTo(nigeria.stocks)
    }

    @Test
    fun `should load built-in logos without waiting for the server list`() = runTest(testDispatcher) {
        val response = CompletableDeferred<Either<Failure, OnboardingSuggestions>>()
        coEvery { onboardingRepository.fetchSuggestions() } coAnswers { response.await() }
        coEvery { companyRepository.getCompanyLogos(any()) } returns
            Either.Right(mapOf("AAPL" to "https://logo/aapl.png"))
        val vm = viewModel()

        advanceUntilIdle()

        assertThat(vm.uiState.value.suggestedStocks.first().logoUrl).isEqualTo("https://logo/aapl.png")
        response.complete(Either.Left(Failure.ServerError))
    }

    @Test
    fun `should keep the list once a stock is tapped`() = runTest(testDispatcher) {
        val response = CompletableDeferred<Either<Failure, OnboardingSuggestions>>()
        coEvery { onboardingRepository.fetchSuggestions() } coAnswers { response.await() }
        val vm = viewModel()
        advanceUntilIdle()
        val shown = vm.uiState.value.suggestedStocks

        vm.onStockSelected("AAPL", "Apple", "suggested", null)
        response.complete(Either.Right(nigeria))
        advanceUntilIdle()

        assertThat(vm.uiState.value.suggestedStocks).isEqualTo(shown)
    }

    @Test
    fun `should keep the built-in list and load its logos when the request fails`() = runTest(testDispatcher) {
        coEvery { onboardingRepository.fetchSuggestions() } returns Either.Left(Failure.ServerError)
        val logos = mapOf("AAPL" to "https://logo/aapl.png")
        coEvery { companyRepository.getCompanyLogos(any()) } returns Either.Right(logos)
        val vm = viewModel()

        advanceUntilIdle()

        val stocks = vm.uiState.value.suggestedStocks
        assertThat(stocks.map { it.ticker }).containsExactly("AAPL", "TSLA", "NVDA", "MSFT", "AMZN", "GOOGL").inOrder()
        assertThat(stocks.first().logoUrl).isEqualTo("https://logo/aapl.png")
    }

    @Test
    fun `should not ask for suggestions without a guest token`() = runTest(testDispatcher) {
        coEvery { authRepository.acquireGuestToken() } returns Result.failure(Exception("no token"))
        coEvery { onboardingRepository.fetchSuggestions() } returns Either.Right(nigeria)
        viewModel()

        advanceUntilIdle()

        coVerify(exactly = 0) { onboardingRepository.fetchSuggestions() }
    }

    @Test
    fun `should log the picker view once with the list source and market`() = runTest(testDispatcher) {
        coEvery { onboardingRepository.fetchSuggestions() } returns Either.Right(nigeria)
        val vm = viewModel()
        advanceUntilIdle()

        vm.openPicker()
        vm.onStockSelected("AAPL", "Apple", "suggested", null)
        vm.onBackToStockSelection()
        vm.onNextScreen()

        verify(exactly = 1) {
            analyticsLogger.logEvent(
                "onboarding-stock-picker-viewed",
                mapOf("suggestions_source" to "network", "suggestions_market" to "NG")
            )
        }
    }

    @Test
    fun `should log the built-in list as default US when nothing loaded`() = runTest(testDispatcher) {
        coEvery { onboardingRepository.fetchSuggestions() } returns Either.Left(Failure.NetworkConnection)
        val vm = viewModel()
        advanceUntilIdle()

        vm.openPicker()

        verify {
            analyticsLogger.logEvent(
                "onboarding-stock-picker-viewed",
                mapOf("suggestions_source" to "default", "suggestions_market" to "US")
            )
        }
    }
}
