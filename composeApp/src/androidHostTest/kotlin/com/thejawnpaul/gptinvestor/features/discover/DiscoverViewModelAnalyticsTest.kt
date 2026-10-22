package com.thejawnpaul.gptinvestor.features.discover

import androidx.lifecycle.SavedStateHandle
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class DiscoverViewModelAnalyticsTest {

    private val testDispatcher = StandardTestDispatcher()
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val appPreferences: AppPreferences = mockk(relaxed = true)
    private val tidbitRepository: TidbitRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { appPreferences.isGuestLoggedIn } returns flowOf(false)
        coEvery { tidbitRepository.getTodayTidbit() } returns Result.failure(Exception())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = DiscoverViewModel(
        companyRepository = mockk<CompanyRepository>(relaxed = true),
        getTopPicksUseCase = mockk<GetTopPicksUseCase>(relaxed = true),
        appPreferences = appPreferences,
        savedStateHandle = SavedStateHandle(),
        tidbitRepository = tidbitRepository,
        analyticsLogger = analyticsLogger
    )

    @Test
    fun `tapping a top pick logs the top pick event`() = runTest(testDispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        vm.handleEvent(DiscoveryEvent.ClickTopPick(id = "pick-1"))

        verify {
            analyticsLogger.logEvent(
                eventName = "discover-top-pick-tapped",
                params = mapOf("top_pick_id" to "pick-1", "user_type" to "authenticated")
            )
        }
    }

    @Test
    fun `tapping the tidbit logs the tidbit event, not the top pick one`() = runTest(testDispatcher) {
        val vm = viewModel()
        advanceUntilIdle()

        vm.handleEvent(DiscoveryEvent.ClickTidbit(id = "tidbit-1"))

        verify {
            analyticsLogger.logEvent(
                eventName = "discover-tidbit-tapped",
                params = mapOf("tidbit_id" to "tidbit-1", "user_type" to "authenticated")
            )
        }
        verify(exactly = 0) { analyticsLogger.logEvent(eventName = "discover-top-pick-tapped", params = any()) }
    }
}
