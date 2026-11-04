package com.thejawnpaul.gptinvestor.features.news.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.news.domain.NewsFailure
import com.thejawnpaul.gptinvestor.features.news.domain.NewsRepository
import com.thejawnpaul.gptinvestor.features.premium.domain.PREMIUM_SOURCE_WATCHLIST_FULL
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistFailure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistBatchResult
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.AddStocksToWatchlistUseCase
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.RemoveStocksFromWatchlistUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
class StoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: NewsRepository = mockk()
    private val appPreferences: AppPreferences = mockk(relaxed = true)
    private val watchlistRepository: WatchlistRepository = mockk()
    private val premiumEventBus: PremiumEventBus = mockk(relaxed = true)
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val guest = MutableStateFlow<Boolean?>(false)
    private val watchlistChanges = MutableSharedFlow<Unit>()

    private val story = testStory(
        id = "s1",
        moved = listOf(testStock("DAL", watched = true), testStock("XOM"), testStock("CVX")),
        barelyMoved = listOf(testStock("KO"))
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { appPreferences.isGuestLoggedIn } returns guest
        coEvery { repository.getStory("s1") } returns Either.Right(story)
        every { watchlistRepository.changes } returns watchlistChanges
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.viewModel() = StoryViewModel(
        newsRepository = repository,
        appPreferences = appPreferences,
        addStocks = AddStocksToWatchlistUseCase(testDispatcher, this, watchlistRepository),
        removeStocks = RemoveStocksFromWatchlistUseCase(testDispatcher, this, watchlistRepository),
        premiumEventBus = premiumEventBus,
        watchlistRepository = watchlistRepository,
        savedStateHandle = SavedStateHandle(mapOf("storyId" to "s1")),
        analyticsLogger = analyticsLogger
    )

    @Test
    fun `should seed stars from the backend's watchlist flags`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.state.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertThat(state.watch.isWatched("DAL")).isTrue()
            assertThat(state.watchAll).isEqualTo(WatchAllUi.AddRest(count = 3, enabled = true, loading = false))
        }
    }

    @Test
    fun `should add the unwatched stocks in one request and offer undo`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.addStocksToWatchlist(listOf("XOM", "CVX", "KO")) } returns
            Either.Right(WatchlistBatchResult(added = listOf("XOM", "CVX"), alreadyWatched = listOf("KO")))
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.WatchAll)
                advanceUntilIdle()
                assertThat(expectMostRecentItem().watchAll).isEqualTo(WatchAllUi.AllWatched(count = 4))
            }
            assertThat(awaitItem()).isEqualTo(StoryAction.Watch(StockWatchEvent.AddedAll(listOf("XOM", "CVX"))))
        }
        coVerify(exactly = 1) { watchlistRepository.addStocksToWatchlist(any()) }
    }

    @Test
    fun `should keep the newest reload when an older one answers last`() = runTest(testDispatcher) {
        val stale =
            testStory(id = "s1", moved = listOf(testStock("DAL", watched = true), testStock("XOM", watched = true)))
        val fresh = testStory(id = "s1", moved = listOf(testStock("DAL", watched = true), testStock("XOM")))
        val replies = ArrayDeque(listOf(0L to story, 1_000L to stale, 10L to fresh))
        coEvery { repository.getStory("s1") } coAnswers {
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

    @Test
    fun `should add nothing and offer premium when the watchlist is full`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.addStocksToWatchlist(any()) } returns
            Either.Left(WatchlistFailure.WatchlistFull(cap = 5, used = 3))
        val vm = viewModel()
        vm.state.test {
            advanceUntilIdle()
            vm.handleEvent(StoryEvent.WatchAll)
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertThat(state.watch.isWatched("XOM")).isFalse()
            assertThat(state.watchAll?.enabled).isTrue()
        }
        verify {
            premiumEventBus.requestPremium(
                source = PREMIUM_SOURCE_WATCHLIST_FULL,
                watchlistCap = 5,
                ticker = null,
                watchlistUsed = 3
            )
        }
    }

    @Test
    fun `should remove the added stocks in one request on undo`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.removeStocksFromWatchlist(listOf("XOM", "CVX")) } returns
            Either.Right(listOf("XOM", "CVX"))
        val vm = viewModel()
        vm.state.test {
            advanceUntilIdle()
            vm.handleEvent(StoryEvent.UndoWatchAll(listOf("XOM", "CVX")))
            advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { watchlistRepository.removeStocksFromWatchlist(listOf("XOM", "CVX")) }
    }

    @Test
    fun `should ask guests to sign in instead of starring`() = runTest(testDispatcher) {
        guest.value = true
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.ToggleWatch("XOM"))
                advanceUntilIdle()
                cancelAndIgnoreRemainingEvents()
            }
            assertThat(awaitItem()).isEqualTo(StoryAction.Watch(StockWatchEvent.SignInRequired))
        }
        coVerify(exactly = 0) { watchlistRepository.addStocksToWatchlist(any()) }
    }

    @Test
    fun `should open chat with the story id and no link`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.AskAboutStory)
                vm.handleEvent(StoryEvent.AskFollowUp("XOM"))
                advanceUntilIdle()
                cancelAndIgnoreRemainingEvents()
            }
            assertThat(awaitItem()).isEqualTo(StoryAction.OpenChat(storyId = "s1", title = story.title, stock = null))
            assertThat(awaitItem()).isEqualTo(
                StoryAction.OpenChat(storyId = "s1", title = story.title, stock = story.moved[1])
            )
        }
    }

    @Test
    fun `should show the unavailable state for an expired story`() = runTest(testDispatcher) {
        coEvery { repository.getStory("s1") } returns Either.Left(NewsFailure.NotFound)
        val vm = viewModel()
        vm.state.test {
            advanceUntilIdle()
            assertThat(expectMostRecentItem().content).isEqualTo(StoryContent.Unavailable)
        }
    }

    @Test
    fun `should resync stars when the watchlist changes on another screen`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.state.test {
            advanceUntilIdle()
            val unwatched = story.copy(moved = story.moved.map { it.copy(isOnWatchlist = false) })
            coEvery { repository.getStory("s1") } returns Either.Right(unwatched)
            watchlistChanges.emit(Unit)
            advanceUntilIdle()
            assertThat(expectMostRecentItem().watch.isWatched("DAL")).isFalse()
        }
    }

    @Test
    fun `should not offer undo when nothing new was added`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.addStocksToWatchlist(any()) } returns
            Either.Right(WatchlistBatchResult(added = emptyList(), alreadyWatched = listOf("XOM", "CVX", "KO")))
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.WatchAll)
                advanceUntilIdle()
                cancelAndIgnoreRemainingEvents()
            }
            expectNoEvents()
        }
    }

    @Test
    fun `should say when undo fails`() = runTest(testDispatcher) {
        coEvery { watchlistRepository.removeStocksFromWatchlist(any()) } returns Either.Left(Failure.NetworkConnection)
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.UndoWatchAll(listOf("XOM")))
                advanceUntilIdle()
                cancelAndIgnoreRemainingEvents()
            }
            assertThat(awaitItem()).isEqualTo(StoryAction.Watch(StockWatchEvent.UndoFailed))
        }
    }

    @Test
    fun `should share text straight away when the backend has no share image`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.Share)
                advanceUntilIdle()
                assertThat(expectMostRecentItem().sharePreview).isNull()
            }
            assertThat(awaitItem()).isEqualTo(StoryAction.ShareText(story))
        }
    }

    @Test
    fun `should preview then share the downloaded image`() = runTest(testDispatcher) {
        val withImage = story.copy(shareImageUrl = "https://cdn/s1.png")
        val png = byteArrayOf(1, 2, 3)
        coEvery { repository.getStory("s1") } returns Either.Right(withImage)
        coEvery { repository.getShareImage("https://cdn/s1.png") } returns Either.Right(png)
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.Share)
                advanceUntilIdle()
                assertThat(expectMostRecentItem().sharePreview?.image).isEqualTo(png)
                vm.handleEvent(StoryEvent.ConfirmShare)
                advanceUntilIdle()
                assertThat(expectMostRecentItem().sharePreview).isNull()
            }
            val action = awaitItem() as StoryAction.ShareImage
            assertThat(action.png).isEqualTo(png)
        }
        coVerify(exactly = 1) { repository.getShareImage("https://cdn/s1.png") }
    }

    @Test
    fun `should cancel the download and share nothing when the preview is closed`() = runTest(testDispatcher) {
        val withImage = story.copy(shareImageUrl = "https://cdn/s1.png")
        coEvery { repository.getStory("s1") } returns Either.Right(withImage)
        coEvery { repository.getShareImage(any()) } coAnswers {
            delay(1_000)
            Either.Right(byteArrayOf(1))
        }
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.Share)
                runCurrent()
                assertThat(expectMostRecentItem().sharePreview?.ready).isFalse()
                vm.handleEvent(StoryEvent.CloseShare)
                advanceUntilIdle()
                assertThat(expectMostRecentItem().sharePreview).isNull()
            }
            expectNoEvents()
        }
    }

    @Test
    fun `should fall back to text when the share image fails to download`() = runTest(testDispatcher) {
        val withImage = story.copy(shareImageUrl = "https://cdn/s1.png")
        coEvery { repository.getStory("s1") } returns Either.Right(withImage)
        coEvery { repository.getShareImage(any()) } returns Either.Left(Failure.NetworkConnection)
        val vm = viewModel()
        vm.actions.test {
            vm.state.test {
                advanceUntilIdle()
                vm.handleEvent(StoryEvent.Share)
                advanceUntilIdle()
                assertThat(expectMostRecentItem().sharePreview).isNull()
                cancelAndIgnoreRemainingEvents()
            }
            assertThat(awaitItem()).isEqualTo(StoryAction.ShareText(withImage))
        }
    }
}
