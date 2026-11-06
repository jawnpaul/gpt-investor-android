package com.thejawnpaul.gptinvestor.features.news.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.news.domain.NewsFailure
import com.thejawnpaul.gptinvestor.features.news.domain.NewsRepository
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.AddStocksToWatchlistUseCase
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.RemoveStocksFromWatchlistUseCase
import kotlin.time.Clock
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class StoryViewModel(
    private val newsRepository: NewsRepository,
    private val appPreferences: AppPreferences,
    addStocks: AddStocksToWatchlistUseCase,
    removeStocks: RemoveStocksFromWatchlistUseCase,
    premiumEventBus: PremiumEventBus,
    private val watchlistRepository: WatchlistRepository,
    savedStateHandle: SavedStateHandle,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val storyId: String = savedStateHandle.get<String>("storyId").orEmpty()

    private val _actions = MutableSharedFlow<StoryAction>()
    val actions = _actions.asSharedFlow()

    private val watch = StockWatchController(
        addStocks = addStocks,
        removeStocks = removeStocks,
        premiumEventBus = premiumEventBus,
        analyticsLogger = analyticsLogger,
        scope = viewModelScope,
        source = "story",
        isGuest = { state.value.isGuestSession },
        onEvent = { _actions.emit(StoryAction.Watch(it)) }
    )

    private val _state = MutableStateFlow(StoryState())
    private var hasLoaded = false
    private var loadJob: Job? = null
    private var shareJob: Job? = null

    val state: StateFlow<StoryState> = combine(_state, watch.state, appPreferences.isGuestLoggedIn) {
            state,
            watch,
            guest
        ->
        state.copy(watch = watch, isGuestSession = guest == true)
    }
        .onStart { if (!hasLoaded) start() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _state.value)

    fun handleEvent(event: StoryEvent) {
        val story = _state.value.story
        when (event) {
            StoryEvent.Back -> emit(StoryAction.GoBack)
            StoryEvent.Retry -> load()
            StoryEvent.ToggleBarelyMoved -> _state.update { it.copy(showBarelyMoved = !it.showBarelyMoved) }
            is StoryEvent.TapStock -> {
                analyticsLogger.logEvent("story-stock-tapped", mapOf("ticker" to event.ticker, "story_id" to storyId))
                _state.update { it.copy(sheetTicker = event.ticker) }
            }

            StoryEvent.CloseSheet -> _state.update { it.copy(sheetTicker = null) }
            is StoryEvent.ToggleWatch -> watch.toggle(event.ticker)
            StoryEvent.WatchAll -> story?.let { watch.addAll(it.allStocks) }
            is StoryEvent.UndoWatchAll -> watch.undo(event.tickers)
            is StoryEvent.ViewCompany -> {
                _state.update { it.copy(sheetTicker = null) }
                emit(StoryAction.OpenCompany(event.ticker))
            }

            is StoryEvent.AskFollowUp -> {
                val stock = story?.allStocks?.firstOrNull { it.ticker == event.ticker } ?: return
                _state.update { it.copy(sheetTicker = null) }
                emit(StoryAction.OpenChat(storyId = story.id, title = story.title, stock = stock))
            }

            StoryEvent.AskAboutStory -> {
                if (story == null) return
                analyticsLogger.logEvent("story-ask-tapped", mapOf("story_id" to story.id))
                emit(StoryAction.OpenChat(storyId = story.id, title = story.title, stock = null))
            }

            StoryEvent.OpenExplainer -> story?.explainer?.let {
                analyticsLogger.logEvent(
                    "explainer-opened",
                    mapOf("story_id" to story.id, "explainer_id" to it.id)
                )
                emit(StoryAction.OpenExplainer(story.id))
            }

            StoryEvent.ReadFullStory -> story?.let { emit(StoryAction.OpenUrl(it.link)) }
            is StoryEvent.OpenSource -> emit(StoryAction.OpenUrl(event.url))
            StoryEvent.Share -> story?.let(::openShare)
            StoryEvent.CloseShare -> closeShare()
            StoryEvent.ConfirmShare -> confirmShare(story)
        }
    }

    private fun start() {
        hasLoaded = true
        load()
        viewModelScope.launch { watchlistRepository.changes.collect { load() } }
    }

    private fun openShare(story: Story) {
        val url = story.shareImageUrl
        if (url == null) {
            shareText(story)
            return
        }
        shareJob?.cancel()
        _state.update { it.copy(sharePreviewOpen = true, shareImage = null) }
        shareJob = viewModelScope.launch {
            newsRepository.getShareImage(url)
                .onSuccess { png -> _state.update { it.copy(shareImage = png) } }
                .onFailure {
                    _state.update { it.copy(sharePreviewOpen = false) }
                    shareText(story)
                }
        }
    }

    private fun closeShare() {
        shareJob?.cancel()
        _state.update { it.copy(sharePreviewOpen = false, shareImage = null) }
    }

    private fun confirmShare(story: Story?) {
        val png = _state.value.shareImage ?: return
        if (story == null) return
        analyticsLogger.logEvent("story-shared", mapOf("story_id" to story.id, "format" to "image"))
        _state.update { it.copy(sharePreviewOpen = false, shareImage = null) }
        emit(StoryAction.ShareImage(png = png, story = story))
    }

    private fun shareText(story: Story) {
        analyticsLogger.logEvent("story-shared", mapOf("story_id" to story.id, "format" to "text"))
        emit(StoryAction.ShareText(story))
    }

    private fun load() {
        _state.update { it.copy(loading = it.story == null, error = false, unavailable = false) }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            newsRepository.getStory(storyId)
                .onSuccess { story ->
                    watch.sync(story.allStocks)
                    _state.update { it.copy(story = story, loading = false, meta = story.meta()) }
                }
                .onFailure { failure ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = it.story == null && failure !is NewsFailure.NotFound,
                            unavailable = it.story == null && failure is NewsFailure.NotFound
                        )
                    }
                }
        }
    }

    private fun Story.meta() = storyMeta(publisher, publishedAt, Clock.System.now().epochSeconds)

    private fun emit(action: StoryAction) {
        viewModelScope.launch { _actions.emit(action) }
    }
}

val Story.allStocks: List<StoryStock>
    get() = moved + barelyMoved

data class StoryState(
    val story: Story? = null,
    val meta: String = "",
    val loading: Boolean = story == null,
    val error: Boolean = false,
    val unavailable: Boolean = false,
    val showBarelyMoved: Boolean = false,
    val sheetTicker: String? = null,
    val sharePreviewOpen: Boolean = false,
    val shareImage: ByteArray? = null,
    val isGuestSession: Boolean = false,
    val watch: StockWatchState = StockWatchState()
) {
    val content: StoryContent
        get() = when {
            story != null -> StoryContent.Loaded(story)
            loading -> StoryContent.Loading
            unavailable -> StoryContent.Unavailable
            else -> StoryContent.Error
        }

    val disclaimer: StoryDisclaimer?
        get() = story?.disclaimer()

    val hasMoved: Boolean
        get() = story?.moved?.isNotEmpty() == true

    val hasStocks: Boolean
        get() = story?.allStocks?.isNotEmpty() == true

    val screenToastVisible: Boolean
        get() = sheetTicker == null

    val barelyMoved: BarelyMovedUi?
        get() = story?.barelyMovedUi()

    val watchAll: WatchAllUi?
        get() = story?.let { watchAllUi(it.allStocks, watch) }

    val sharePreview: SharePreviewUi?
        get() = if (sharePreviewOpen) SharePreviewUi(shareImage) else null

    val sheet: StockSheetUi?
        get() {
            val current = story ?: return null
            val ticker = sheetTicker ?: return null
            return current.allStocks.firstOrNull { it.ticker == ticker }?.sheetUi(current, watch)
        }
}

sealed interface StoryContent {
    data object Loading : StoryContent
    data object Unavailable : StoryContent
    data object Error : StoryContent
    data class Loaded(val story: Story) : StoryContent
}

sealed interface StoryEvent {
    data object Back : StoryEvent
    data object Retry : StoryEvent
    data object ToggleBarelyMoved : StoryEvent
    data class TapStock(val ticker: String) : StoryEvent
    data object CloseSheet : StoryEvent
    data class ToggleWatch(val ticker: String) : StoryEvent
    data object WatchAll : StoryEvent
    data class UndoWatchAll(val tickers: List<String>) : StoryEvent
    data class ViewCompany(val ticker: String) : StoryEvent
    data class AskFollowUp(val ticker: String) : StoryEvent
    data object AskAboutStory : StoryEvent
    data object OpenExplainer : StoryEvent
    data object ReadFullStory : StoryEvent
    data class OpenSource(val url: String) : StoryEvent
    data object Share : StoryEvent
    data object CloseShare : StoryEvent
    data object ConfirmShare : StoryEvent
}

sealed interface StoryAction {
    data object GoBack : StoryAction
    data class OpenCompany(val ticker: String) : StoryAction
    data class OpenChat(val storyId: String, val title: String, val stock: StoryStock?) : StoryAction
    data class OpenExplainer(val storyId: String) : StoryAction
    data class OpenUrl(val url: String) : StoryAction
    class ShareImage(val png: ByteArray, val story: Story) : StoryAction
    data class ShareText(val story: Story) : StoryAction
    data class Watch(val event: StockWatchEvent) : StoryAction
}
