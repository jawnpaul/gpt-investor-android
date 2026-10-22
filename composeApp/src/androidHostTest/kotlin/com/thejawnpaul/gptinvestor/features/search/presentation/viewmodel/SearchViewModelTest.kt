package com.thejawnpaul.gptinvestor.features.search.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.search.domain.model.SearchSection
import com.thejawnpaul.gptinvestor.features.search.domain.repository.ISearchRepository
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val searchRepository: ISearchRepository = mockk()
    private val recents =
        SearchSection.RecentList(id = "recent", title = "Recent", clearable = true, items = emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { searchRepository.search(null) } returns flowOf(Either.Right(listOf(recents)))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `a failed search clears the old sections so the error shows`() = runTest(testDispatcher) {
        coEvery { searchRepository.search("nvda") } returns flowOf(Either.Left(Failure.NetworkConnection))
        val vm = SearchViewModel(searchRepository, mockk<AnalyticsLogger>(relaxed = true))
        backgroundScope.launch { vm.uiState.collect { } }
        advanceUntilIdle()
        assertThat(vm.uiState.value.sections).containsExactly(recents)

        vm.handleEvent(SearchEvent.OnQueryChange("nvda"))
        advanceUntilIdle()

        assertThat(vm.uiState.value.sections).isEmpty()
        assertThat(vm.uiState.value.error).isNotNull()
    }
}
