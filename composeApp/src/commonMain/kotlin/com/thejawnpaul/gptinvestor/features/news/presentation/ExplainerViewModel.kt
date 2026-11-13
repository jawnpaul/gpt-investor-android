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
import com.thejawnpaul.gptinvestor.features.news.domain.model.Explainer
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.AddStocksToWatchlistUseCase
import com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases.RemoveStocksFromWatchlistUseCase
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
class ExplainerViewModel(
    private val newsRepository: NewsRepository,
    private val tidbitRepository: TidbitRepository,
    private val appPreferences: AppPreferences,
    addStocks: AddStocksToWatchlistUseCase,
    removeStocks: RemoveStocksFromWatchlistUseCase,
    premiumEventBus: PremiumEventBus,
    private val watchlistRepository: WatchlistRepository,
    savedStateHandle: SavedStateHandle,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val storyId: String = savedStateHandle.get<String>("storyId").orEmpty()

    private val _actions = MutableSharedFlow<ExplainerAction>()
    val actions = _actions.asSharedFlow()

    private val _state = MutableStateFlow(ExplainerState())

    private val watch = StockWatchController(
        addStocks = addStocks,
        removeStocks = removeStocks,
        premiumEventBus = premiumEventBus,
        analyticsLogger = analyticsLogger,
        scope = viewModelScope,
        source = "explainer",
        isGuest = { state.value.isGuestSession },
        onEvent = { _actions.emit(ExplainerAction.Watch(it)) }
    )

    private var hasLoaded = false
    private var loadJob: Job? = null

    val state: StateFlow<ExplainerState> = combine(_state, watch.state, appPreferences.isGuestLoggedIn) {
            state,
            watch,
            guest
        ->
        state.copy(watch = watch, isGuestSession = guest == true)
    }
        .onStart { if (!hasLoaded) start() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _state.value)

    fun handleEvent(event: ExplainerEvent) {
        val explainer = _state.value.explainer
        when (event) {
            ExplainerEvent.Back -> emit(ExplainerAction.GoBack)
            ExplainerEvent.Retry -> load()
            ExplainerEvent.ReadStory -> emit(ExplainerAction.GoBack)
            is ExplainerEvent.ToggleWatch -> watch.toggle(event.ticker)
            ExplainerEvent.WatchAll -> explainer?.let { watch.addAll(it.stocks) }
            is ExplainerEvent.UndoWatchAll -> watch.undo(event.tickers)
            is ExplainerEvent.OpenStock -> emit(ExplainerAction.OpenCompany(event.ticker))
            is ExplainerEvent.OpenSource -> emit(ExplainerAction.OpenUrl(event.url))
            is ExplainerEvent.SetHelpful -> explainer?.let {
                _state.update { s -> s.copy(isLiked = event.helpful) }
                viewModelScope.launch {
                    val result = if (event.helpful) {
                        tidbitRepository.likeTidbit(
                            it.id
                        )
                    } else {
                        tidbitRepository.unlikeTidbit(it.id)
                    }
                    if (result.isFailure) _state.update { s -> s.copy(isLiked = !event.helpful) }
                }
            }

            is ExplainerEvent.SetSaved -> explainer?.let {
                _state.update { s -> s.copy(isBookmarked = event.saved) }
                viewModelScope.launch {
                    val result = if (event.saved) {
                        tidbitRepository.bookmarkTidbit(
                            it.id
                        )
                    } else {
                        tidbitRepository.removeBookmark(it.id)
                    }
                    if (result.isFailure) _state.update { s -> s.copy(isBookmarked = !event.saved) }
                }
            }

            ExplainerEvent.Share -> explainer?.let {
                analyticsLogger.logEvent("explainer-shared", mapOf("explainer_id" to it.id, "story_id" to storyId))
                emit(ExplainerAction.Share(title = it.title, link = it.storyLink))
            }
        }
    }

    private fun start() {
        hasLoaded = true
        load()
        viewModelScope.launch { watchlistRepository.changes.collect { load() } }
    }

    private fun load() {
        _state.update { it.copy(loading = it.explainer == null, error = false, unavailable = false) }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            newsRepository.getExplainer(storyId)
                .onSuccess { explainer ->
                    watch.sync(explainer.stocks)
                    _state.update {
                        it.copy(
                            explainer = explainer,
                            sections = explainer.sections.map { section -> section.toUi() },
                            updatedTime = newsClock(explainer.updatedAt),
                            loading = false,
                            isLiked = explainer.isLiked,
                            isBookmarked = explainer.isBookmarked
                        )
                    }
                }
                .onFailure { failure ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = it.explainer == null && failure !is NewsFailure.NotFound,
                            unavailable = it.explainer == null && failure is NewsFailure.NotFound
                        )
                    }
                }
        }
    }

    private fun emit(action: ExplainerAction) {
        viewModelScope.launch { _actions.emit(action) }
    }
}

data class ExplainerState(
    val explainer: Explainer? = null,
    val sections: List<ExplainerSectionUi> = emptyList(),
    val updatedTime: String = "",
    val loading: Boolean = true,
    val error: Boolean = false,
    val unavailable: Boolean = false,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isGuestSession: Boolean = false,
    val watch: StockWatchState = StockWatchState()
) {
    val stocks: List<StoryStock>
        get() = explainer?.stocks.orEmpty()

    val content: ExplainerContent
        get() = when {
            explainer != null -> ExplainerContent.Loaded(explainer)
            loading -> ExplainerContent.Loading
            unavailable -> ExplainerContent.Unavailable
            else -> ExplainerContent.Error
        }

    val watchAll: WatchAllUi?
        get() = watchAllUi(stocks, watch)
}

sealed interface ExplainerEvent {
    data object Back : ExplainerEvent
    data object Retry : ExplainerEvent
    data object ReadStory : ExplainerEvent
    data class ToggleWatch(val ticker: String) : ExplainerEvent
    data object WatchAll : ExplainerEvent
    data class UndoWatchAll(val tickers: List<String>) : ExplainerEvent
    data class OpenStock(val ticker: String) : ExplainerEvent
    data class OpenSource(val url: String) : ExplainerEvent
    data class SetHelpful(val helpful: Boolean) : ExplainerEvent
    data class SetSaved(val saved: Boolean) : ExplainerEvent
    data object Share : ExplainerEvent
}

sealed interface ExplainerAction {
    data object GoBack : ExplainerAction
    data class OpenCompany(val ticker: String) : ExplainerAction
    data class OpenUrl(val url: String) : ExplainerAction
    data class Share(val title: String, val link: String) : ExplainerAction
    data class Watch(val event: StockWatchEvent) : ExplainerAction
}

sealed interface ExplainerContent {
    data object Loading : ExplainerContent
    data object Unavailable : ExplainerContent
    data object Error : ExplainerContent
    data class Loaded(val explainer: Explainer) : ExplainerContent
}
