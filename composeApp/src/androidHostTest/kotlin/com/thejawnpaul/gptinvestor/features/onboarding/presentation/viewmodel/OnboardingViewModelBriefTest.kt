package com.thejawnpaul.gptinvestor.features.onboarding.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyBriefUseCase
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingRepository
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.BriefView
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
class OnboardingViewModelBriefTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getCompanyBriefUseCase: GetCompanyBriefUseCase = mockk()
    private val appPreferences: AppPreferences = mockk(relaxed = true)
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val authRepository: AuthenticationRepository = mockk(relaxed = true)
    private val onboardingRepository: IOnboardingRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { appPreferences.isUserLoggedIn } returns flowOf(false)
        coEvery { authRepository.acquireGuestToken() } returns Result.success(Unit)
        coEvery { onboardingRepository.fetchSuggestions() } returns Either.Left(Failure.NetworkConnection)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = OnboardingViewModel(
        getCompanyBriefUseCase = getCompanyBriefUseCase,
        companyRepository = mockk<CompanyRepository>(relaxed = true),
        appPreferences = appPreferences,
        authRepository = authRepository,
        onboardingRepository = onboardingRepository,
        analyticsLogger = analyticsLogger
    )

    private fun stubBrief(result: Either<Failure, CompanyBrief>) {
        every { getCompanyBriefUseCase(any(), any()) } answers {
            secondArg<(Either<Failure, CompanyBrief>) -> Unit>()(result)
            Job()
        }
    }

    @Test
    fun `guest limit shows the limit reached state`() = runTest(testDispatcher) {
        stubBrief(Either.Left(Failure.GuestLimitReached))
        val vm = viewModel()

        vm.onStockSelected("TSLA", "Tesla", "suggested", null)
        advanceUntilIdle()

        assertThat(vm.uiState.value.briefView).isEqualTo(BriefView.LimitReached)
        assertThat(vm.uiState.value.briefTitle).isNull()
        verify {
            analyticsLogger.logEvent(
                "onboarding-brief-failed",
                mapOf("ticker" to "TSLA", "error_type" to "guest_limit")
            )
        }
    }

    @Test
    fun `server error keeps the retryable error state`() = runTest(testDispatcher) {
        stubBrief(Either.Left(Failure.ServerError))
        val vm = viewModel()

        vm.onStockSelected("TSLA", "Tesla", "suggested", null)
        advanceUntilIdle()

        assertThat(vm.uiState.value.briefView).isInstanceOf(BriefView.Error::class.java)
    }

    @Test
    fun `a failed guest token is fetched again before the brief`() = runTest(testDispatcher) {
        coEvery { authRepository.acquireGuestToken() } returnsMany listOf(
            Result.failure(Exception("guest-login failed")),
            Result.success(Unit)
        )
        stubBrief(Either.Left(Failure.ServerError))
        val vm = viewModel()

        vm.onStockSelected("TSLA", "Tesla", "suggested", null)
        advanceUntilIdle()

        coVerify(exactly = 2) { authRepository.acquireGuestToken() }
        verify(exactly = 1) { getCompanyBriefUseCase("TSLA", any()) }
    }

    @Test
    fun `no guest token shows the retryable error without requesting the brief`() = runTest(testDispatcher) {
        coEvery { authRepository.acquireGuestToken() } returns Result.failure(Exception("guest-login failed"))
        stubBrief(Either.Left(Failure.ServerError))
        val vm = viewModel()

        vm.onStockSelected("TSLA", "Tesla", "suggested", null)
        advanceUntilIdle()

        assertThat(vm.uiState.value.briefView).isInstanceOf(BriefView.Error::class.java)
        verify(exactly = 0) { getCompanyBriefUseCase(any(), any()) }
        verify {
            analyticsLogger.logEvent(
                "onboarding-brief-failed",
                mapOf("ticker" to "TSLA", "error_type" to "guest_token")
            )
        }

        coEvery { authRepository.acquireGuestToken() } returns Result.success(Unit)
        vm.onRetryBrief()
        advanceUntilIdle()

        verify(exactly = 1) { getCompanyBriefUseCase("TSLA", any()) }
    }

    @Test
    fun `retry after a rejected guest token fetches a new one`() = runTest(testDispatcher) {
        stubBrief(Either.Left(Failure.GuestSessionExpired))
        val vm = viewModel()

        vm.onStockSelected("TSLA", "Tesla", "suggested", null)
        advanceUntilIdle()
        coVerify(exactly = 1) { authRepository.acquireGuestToken() }

        vm.onRetryBrief()
        advanceUntilIdle()

        coVerify(exactly = 2) { authRepository.acquireGuestToken() }
        verify(exactly = 2) { getCompanyBriefUseCase("TSLA", any()) }
    }
}
