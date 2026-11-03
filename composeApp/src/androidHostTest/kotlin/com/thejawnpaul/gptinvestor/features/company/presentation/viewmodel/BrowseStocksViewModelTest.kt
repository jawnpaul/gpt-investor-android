package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.domain.model.Market
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
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
class BrowseStocksViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ICompanyRepository = mockk(relaxed = true)
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val queries = mutableListOf<SearchCompanyQuery>()

    private val nigeria = Market(key = "NG", name = "Nigeria", count = 47)
    private val unitedStates = Market(key = "US", name = "United States", count = 503)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAllSector() } returns flowOf(Either.Right(listOf(SectorInput.AllSector)))
        every { repository.searchCompaniesPaged(capture(queries), any()) } returns flowOf(PagingData.empty())
        every { repository.browseMarket } returns flowOf(null)
        coEvery { repository.getMarkets(any()) } returns Either.Right(listOf(nigeria, unitedStates))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.subscribedViewModel(): BrowseStocksViewModel {
        val vm = BrowseStocksViewModel(SavedStateHandle(), repository, analyticsLogger)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.companiesPaging.collect { } }
        advanceUntilIdle()
        return vm
    }

    @Test
    fun `should show the market filter only with two or more markets`() = runTest(testDispatcher) {
        coEvery { repository.getMarkets(any()) } returns Either.Right(listOf(unitedStates))

        val vm = subscribedViewModel()

        assertThat(vm.state.value.showMarketFilter).isFalse()
        assertThat(vm.state.value.countLine).isNull()
    }

    @Test
    fun `should start on all markets when nothing is saved`() = runTest(testDispatcher) {
        val vm = subscribedViewModel()

        assertThat(vm.state.value.showMarketFilter).isTrue()
        assertThat(vm.state.value.selectedMarket).isNull()
        assertThat(vm.state.value.allMarketsCount).isEqualTo(550)
        assertThat(queries.single().market).isNull()
    }

    @Test
    fun `should load the saved market once without loading all markets first`() = runTest(testDispatcher) {
        every { repository.browseMarket } returns flowOf("NG")

        val vm = subscribedViewModel()

        assertThat(vm.state.value.selectedMarket).isEqualTo(nigeria)
        assertThat(queries.map { it.market }).containsExactly("NG")
    }

    @Test
    fun `should drop a saved market this build can no longer see`() = runTest(testDispatcher) {
        every { repository.browseMarket } returns flowOf("NG")
        coEvery { repository.getMarkets(any()) } returns Either.Right(listOf(unitedStates))

        val vm = subscribedViewModel()

        assertThat(vm.state.value.selectedMarketKey).isNull()
        assertThat(queries.last().market).isNull()
        coVerify { repository.setBrowseMarket(null) }
    }

    @Test
    fun `should show all markets without forgetting the saved one when the market list fails`() =
        runTest(testDispatcher) {
            every { repository.browseMarket } returns flowOf("NG")
            coEvery { repository.getMarkets(any()) } returns Either.Left(Failure.NetworkConnection)

            val vm = subscribedViewModel()

            assertThat(vm.state.value.selectedMarketKey).isNull()
            assertThat(queries.last().market).isNull()
            coVerify(exactly = 0) { repository.setBrowseMarket(any()) }
        }

    @Test
    fun `should keep the saved market when a sector's market list leaves it out`() = runTest(testDispatcher) {
        every { repository.browseMarket } returns flowOf("NG")
        val vm = subscribedViewModel()
        coEvery { repository.getMarkets("energy") } returns Either.Right(listOf(unitedStates))

        vm.handleEvent(BrowseStocksEvent.SelectSector(SectorInput.CustomSector("Energy", "energy")))
        advanceUntilIdle()

        assertThat(vm.state.value.selectedMarketKey).isEqualTo("NG")
        assertThat(queries.last().market).isEqualTo("NG")
        coVerify(exactly = 0) { repository.setBrowseMarket(any()) }
    }

    @Test
    fun `should filter, save and log when a market is selected`() = runTest(testDispatcher) {
        val vm = subscribedViewModel()

        vm.handleEvent(BrowseStocksEvent.OpenMarketSheet)
        vm.handleEvent(BrowseStocksEvent.SelectMarket("NG"))
        advanceUntilIdle()

        assertThat(vm.state.value.isMarketSheetOpen).isFalse()
        assertThat(vm.state.value.selectedMarket).isEqualTo(nigeria)
        assertThat(queries.last().market).isEqualTo("NG")
        coVerify { repository.setBrowseMarket("NG") }
        verify { analyticsLogger.logEvent("browse-market-sheet-opened", mapOf("market" to "all")) }
        verify { analyticsLogger.logEvent("browse-market-selected", mapOf("market" to "NG")) }
    }

    @Test
    fun `should reload market counts for the chosen sector`() = runTest(testDispatcher) {
        val vm = subscribedViewModel()

        vm.handleEvent(BrowseStocksEvent.SelectSector(SectorInput.CustomSector("Energy", "energy")))
        advanceUntilIdle()

        coVerify { repository.getMarkets("energy") }
        assertThat(queries.last().sector).isEqualTo("energy")
    }

    @Test
    fun `should show the count line from the companies total`() = runTest(testDispatcher) {
        every { repository.searchCompaniesPaged(any(), any()) } answers {
            secondArg<(Int?) -> Unit>().invoke(47)
            emptyFlow()
        }

        val vm = subscribedViewModel()

        assertThat(vm.state.value.countLine).isEqualTo(47)
    }
}
