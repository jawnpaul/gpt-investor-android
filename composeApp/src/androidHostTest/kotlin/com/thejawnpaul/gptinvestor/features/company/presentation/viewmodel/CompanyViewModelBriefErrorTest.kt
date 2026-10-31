package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.brief_error_body
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.session.GuestSessionNotifier
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyBriefUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CompanyViewModelBriefErrorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getCompanyBriefUseCase: GetCompanyBriefUseCase = mockk(relaxed = true)
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

    private fun TestScope.viewModelFailingWith(failure: Failure): CompanyViewModel {
        every { getCompanyBriefUseCase(any(), any()) } answers {
            secondArg<(Either<Failure, CompanyBrief>) -> Unit>().invoke(Either.Left(failure))
            mockk(relaxed = true)
        }
        val vm = CompanyViewModel(
            getCompanyBriefUseCase = getCompanyBriefUseCase,
            getCompanyUseCase = mockk<GetCompanyUseCase>(relaxed = true),
            savedStateHandle = SavedStateHandle(mapOf("ticker" to "INTENEGINS.NG")),
            getInputPromptUseCase = mockk<GetInputPromptUseCase>(relaxed = true),
            modelsRepository = modelsRepository,
            feedbackRepository = mockk<FeedbackRepository>(relaxed = true),
            appPreferences = appPreferences,
            watchlistRepository = mockk<WatchlistRepository>(relaxed = true),
            premiumEventBus = PremiumEventBus(),
            analyticsLogger = mockk<AnalyticsLogger>(relaxed = true),
            guestSessionNotifier = mockk<GuestSessionNotifier>(relaxed = true)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.selectedCompany.collect { } }
        advanceUntilIdle()
        return vm
    }

    @Test
    fun `should ask to check the connection when the brief fails offline`() = runTest(testDispatcher) {
        val vm = viewModelFailingWith(Failure.NetworkConnection)

        assertThat(vm.selectedCompany.value.error).isNotNull()
        assertThat(vm.selectedCompany.value.errorBody).isEqualTo(Res.string.check_connection_try_again)
    }

    @Test
    fun `should not blame the connection when the brief response is unreadable`() = runTest(testDispatcher) {
        val vm = viewModelFailingWith(Failure.DataError)

        assertThat(vm.selectedCompany.value.error).isNotNull()
        assertThat(vm.selectedCompany.value.errorBody).isEqualTo(Res.string.brief_error_body)
    }
}
