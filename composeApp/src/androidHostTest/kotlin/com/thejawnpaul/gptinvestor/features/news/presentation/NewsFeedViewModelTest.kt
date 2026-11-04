package com.thejawnpaul.gptinvestor.features.news.presentation

import androidx.paging.LoadState
import androidx.paging.PagingData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.news.domain.NewsRepository
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsFeedMeta
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsTopic
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
class NewsFeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: NewsRepository = mockk()
    private val appPreferences: AppPreferences = mockk(relaxed = true)
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val lastSeen = MutableStateFlow<Long?>(null)
    private val guest = MutableStateFlow<Boolean?>(false)
    private val energy = NewsTopic(id = "energy", label = "Energy")
    private val meta = NewsFeedMeta(topics = listOf(energy), latestPublishedAt = 500L, personalised = true)

    private val loading = LoadState.Loading
    private val loaded = LoadState.NotLoading(endOfPaginationReached = false)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { appPreferences.newsLastSeenAt } returns lastSeen
        every { appPreferences.isGuestLoggedIn } returns guest
        every { repository.getFeedPaged(any(), any()) } answers {
            secondArg<(NewsFeedMeta) -> Unit>().invoke(meta)
            flowOf(PagingData.from(listOf(testStory("s1"))))
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = NewsFeedViewModel(repository, appPreferences, analyticsLogger)

    @Test
    fun `should take topics and the new-stories time from the first page`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.state.test {
            vm.stories.first()
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertThat(state.filters)
                .containsExactly(NewsFilter.All, NewsFilter.ForYou, NewsFilter.Topic(energy))
                .inOrder()
            assertThat(state.hasNewStories).isTrue()
        }
        verify { repository.getFeedPaged("all", any()) }
    }

    @Test
    fun `should page the selected filter from the backend`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.handleEvent(NewsFeedEvent.SelectFilter(NewsFilter.Topic(energy)))
        vm.stories.first()

        verify { repository.getFeedPaged("energy", any()) }
    }

    @Test
    fun `should hide for you from guests`() = runTest(testDispatcher) {
        guest.value = true
        val vm = viewModel()
        vm.state.test {
            vm.stories.first()
            advanceUntilIdle()
            assertThat(expectMostRecentItem().filters)
                .containsExactly(NewsFilter.All, NewsFilter.Topic(energy))
                .inOrder()
        }
    }

    @Test
    fun `should mark the newest story as seen`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.state.test {
            vm.stories.first()
            advanceUntilIdle()
            vm.handleEvent(NewsFeedEvent.MarkSeen)
            advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { appPreferences.setNewsLastSeenAt(500L) }
    }

    @Test
    fun `should pick the feed content from the load state`() {
        val all = NewsFeedState()
        val topic = NewsFeedState(filter = NewsFilter.Topic(energy))
        val forYou = NewsFeedState(filter = NewsFilter.ForYou)

        assertThat(all.content(loading, storyCount = 0)).isEqualTo(NewsFeedContent.Loading)
        assertThat(all.content(LoadState.Error(Exception()), storyCount = 0)).isEqualTo(NewsFeedContent.Error)
        assertThat(all.content(loaded, storyCount = 0)).isEqualTo(NewsFeedContent.Empty)
        assertThat(topic.content(loaded, storyCount = 0)).isEqualTo(NewsFeedContent.FilterEmpty)
        assertThat(all.content(loading, storyCount = 3)).isEqualTo(NewsFeedContent.Loading)
        assertThat(all.content(loaded, storyCount = 3)).isEqualTo(NewsFeedContent.Stories(showForYouStrip = false))
        assertThat(forYou.content(loaded, storyCount = 3)).isEqualTo(NewsFeedContent.Stories(showForYouStrip = true))
        assertThat(forYou.copy(personalised = false).content(loaded, storyCount = 2))
            .isEqualTo(NewsFeedContent.ColdStart(showPopularHeader = true))
    }

    @Test
    fun `should build card text and why tags only for for you`() {
        val story = testStory(
            id = "s2",
            moved = listOf(testStock("DAL"), testStock("XOM"), testStock("CVX"), testStock("UPS"))
        ).copy(watchedTickers = listOf("DAL"), movedCount = 5, connectedCount = 9)

        val forYou = story.toCard(now = 1_000L, showWhy = true)
        val all = story.toCard(now = 1_000L, showWhy = false)

        assertThat(forYou.movers.map { it.ticker }).containsExactly("DAL", "XOM", "CVX").inOrder()
        assertThat(forYou.moverNames).isEqualTo("DAL, XOM, CVX")
        assertThat(forYou.moreMovers).isEqualTo(2)
        assertThat(forYou.watchedTicker).isEqualTo("DAL")
        assertThat(forYou.topic?.label).isEqualTo("Energy")
        assertThat(all.hasWhyTags).isFalse()
    }
}
