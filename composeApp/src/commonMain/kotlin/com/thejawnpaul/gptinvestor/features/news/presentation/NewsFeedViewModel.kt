package com.thejawnpaul.gptinvestor.features.news.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.news.domain.NewsRepository
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsTopic
import kotlin.time.Clock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class NewsFeedViewModel(
    private val newsRepository: NewsRepository,
    private val appPreferences: AppPreferences,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _state = MutableStateFlow(NewsFeedState())

    val state: StateFlow<NewsFeedState> = combine(
        _state,
        appPreferences.newsLastSeenAt,
        appPreferences.isGuestLoggedIn
    ) { state, seen, guest ->
        state.copy(lastSeenAt = seen ?: 0L, isGuestSession = guest == true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsFeedState())

    private val filter = MutableStateFlow<NewsFilter>(NewsFilter.All)

    @OptIn(ExperimentalCoroutinesApi::class)
    val stories: Flow<PagingData<StoryCardUi>> = filter
        .flatMapLatest { selected ->
            newsRepository.getFeedPaged(selected.id) { meta ->
                if (filter.value == selected) {
                    _state.update {
                        it.copy(
                            topics = meta.topics.ifEmpty { it.topics },
                            personalised = meta.personalised,
                            latestPublishedAt = maxOf(it.latestPublishedAt, meta.latestPublishedAt)
                        )
                    }
                }
            }.map { page ->
                val now = Clock.System.now().epochSeconds
                val seen = mutableSetOf<String>()
                page.filter { seen.add(it.id) }.map { it.toCard(now, showWhy = selected == NewsFilter.ForYou) }
            }
        }
        .cachedIn(viewModelScope)

    private val _actions = MutableSharedFlow<NewsFeedAction>()
    val actions = _actions.asSharedFlow()

    fun handleEvent(event: NewsFeedEvent) {
        when (event) {
            is NewsFeedEvent.SelectFilter -> {
                if (event.filter == filter.value) return
                filter.value = event.filter
                _state.update { it.copy(filter = event.filter) }
                analyticsLogger.logEvent("news-filter-selected", mapOf("filter" to event.filter.id))
            }

            is NewsFeedEvent.OpenStory -> {
                analyticsLogger.logEvent(
                    "story-opened",
                    mapOf("story_id" to event.id, "source" to "discover", "filter" to filter.value.id)
                )
                emit(NewsFeedAction.OpenStory(event.id))
            }

            NewsFeedEvent.PasteLink -> emit(NewsFeedAction.OpenChat)
            NewsFeedEvent.ManageWatchlist -> emit(NewsFeedAction.OpenWatchlist)
            NewsFeedEvent.AddStocks -> emit(NewsFeedAction.OpenBrowse)
            NewsFeedEvent.ShowAll -> handleEvent(NewsFeedEvent.SelectFilter(NewsFilter.All))
            NewsFeedEvent.MarkSeen -> {
                val latest = _state.value.latestPublishedAt
                if (latest > 0L) viewModelScope.launch { appPreferences.setNewsLastSeenAt(latest) }
            }
        }
    }

    private fun emit(action: NewsFeedAction) {
        viewModelScope.launch { _actions.emit(action) }
    }
}

sealed interface NewsFeedContent {
    data object Loading : NewsFeedContent
    data object Error : NewsFeedContent
    data object Empty : NewsFeedContent
    data object FilterEmpty : NewsFeedContent
    data class ColdStart(val showPopularHeader: Boolean) : NewsFeedContent
    data class Stories(val showForYouStrip: Boolean) : NewsFeedContent
}

data class NewsFeedState(
    val filter: NewsFilter = NewsFilter.All,
    val topics: List<NewsTopic> = emptyList(),
    val personalised: Boolean = true,
    val latestPublishedAt: Long = 0L,
    val isGuestSession: Boolean = false,
    val lastSeenAt: Long = 0L
) {
    val hasNewStories: Boolean
        get() = latestPublishedAt > lastSeenAt

    val filters: List<NewsFilter>
        get() = buildList {
            add(NewsFilter.All)
            if (!isGuestSession) add(NewsFilter.ForYou)
            topics.mapTo(this, NewsFilter::Topic)
        }

    fun content(refresh: LoadState, storyCount: Int): NewsFeedContent = when {
        refresh is LoadState.Loading -> NewsFeedContent.Loading
        refresh is LoadState.Error -> NewsFeedContent.Error
        filter == NewsFilter.ForYou && !personalised -> NewsFeedContent.ColdStart(showPopularHeader = storyCount > 0)
        storyCount == 0 && filter == NewsFilter.All -> NewsFeedContent.Empty
        storyCount == 0 -> NewsFeedContent.FilterEmpty
        else -> NewsFeedContent.Stories(showForYouStrip = filter == NewsFilter.ForYou)
    }
}

sealed interface NewsFeedEvent {
    data class SelectFilter(val filter: NewsFilter) : NewsFeedEvent
    data class OpenStory(val id: String) : NewsFeedEvent
    data object PasteLink : NewsFeedEvent
    data object ManageWatchlist : NewsFeedEvent
    data object AddStocks : NewsFeedEvent
    data object ShowAll : NewsFeedEvent
    data object MarkSeen : NewsFeedEvent
}

sealed interface NewsFeedAction {
    data class OpenStory(val id: String) : NewsFeedAction
    data object OpenChat : NewsFeedAction
    data object OpenWatchlist : NewsFeedAction
    data object OpenBrowse : NewsFeedAction
}
