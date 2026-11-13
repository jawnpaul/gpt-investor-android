package com.thejawnpaul.gptinvestor.features.news.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.news.domain.NewsRepository
import com.thejawnpaul.gptinvestor.features.news.domain.model.Explainer
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.AddStocksToWatchlistUseCase
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.RemoveStocksFromWatchlistUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExplainerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: NewsRepository = mockk()
    private val appPreferences: AppPreferences = mockk(relaxed = true)
    private val watchlistRepository: WatchlistRepository = mockk()
    private val watchlistChanges = MutableSharedFlow<Unit>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { appPreferences.isGuestLoggedIn } returns MutableStateFlow(false)
        every { watchlistRepository.changes } returns watchlistChanges
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.viewModel() = ExplainerViewModel(
        newsRepository = repository,
        tidbitRepository = mockk<TidbitRepository>(relaxed = true),
        appPreferences = appPreferences,
        addStocks = AddStocksToWatchlistUseCase(testDispatcher, this, watchlistRepository),
        removeStocks = RemoveStocksFromWatchlistUseCase(testDispatcher, this, watchlistRepository),
        premiumEventBus = mockk<PremiumEventBus>(relaxed = true),
        watchlistRepository = watchlistRepository,
        savedStateHandle = SavedStateHandle(mapOf("storyId" to "s1")),
        analyticsLogger = mockk<AnalyticsLogger>(relaxed = true)
    )

    private fun explainer(xomWatched: Boolean) = Explainer(
        id = "e1",
        title = "Why oil moves airlines",
        author = "GPT Investor",
        storyLink = "https://example.com/s1",
        readMinutes = 3,
        updatedAt = 0L,
        sections = emptyList(),
        sources = emptyList(),
        stocks = listOf(testStock("DAL", watched = true), testStock("XOM", watched = xomWatched)),
        isLiked = false,
        isBookmarked = false
    )

    @Test
    fun `should keep the newest reload when an older one answers last`() = runTest(testDispatcher) {
        val replies = ArrayDeque(
            listOf(0L to explainer(xomWatched = false), 1_000L to explainer(xomWatched = true), 10L to explainer(false))
        )
        coEvery { repository.getExplainer("s1") } coAnswers {
            val (wait, reply) = replies.removeFirst()
            delay(wait)
            Either.Right(reply)
        }
        val vm = viewModel()
        vm.state.test {
            advanceUntilIdle()
            watchlistChanges.emit(Unit)
            runCurrent()
            watchlistChanges.emit(Unit)
            advanceUntilIdle()
            assertThat(expectMostRecentItem().watch.isWatched("XOM")).isFalse()
        }
    }
}
