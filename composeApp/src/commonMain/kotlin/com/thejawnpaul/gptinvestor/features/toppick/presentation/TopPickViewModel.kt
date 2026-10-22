package com.thejawnpaul.gptinvestor.features.toppick.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.session.GuestRateLimitNotifier
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyBriefUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.thejawnpaul.gptinvestor.features.premium.domain.PREMIUM_SOURCE_WATCHLIST_FULL
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetLocalTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetSingleTopPickUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.RemoveTopPickFromSavedUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.SaveTopPickUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.ShareTopPickUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickAction.ShowToast
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPickDetailView
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistFailure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class TopPickViewModel(
    private val getSingleTopPickUseCase: GetSingleTopPickUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getLocalTopPicksUseCase: GetLocalTopPicksUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val saveTopPickUseCase: SaveTopPickUseCase,
    private val removeTopPickFromSavedUseCase: RemoveTopPickFromSavedUseCase,
    private val shareTopPickUseCase: ShareTopPickUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val appPreferences: AppPreferences,
    private val guestRateLimitNotifier: GuestRateLimitNotifier,
    private val getCompanyBriefUseCase: GetCompanyBriefUseCase,
    private val watchlistRepository: WatchlistRepository,
    private val premiumEventBus: PremiumEventBus,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val topPickId: String?
        get() = savedStateHandle.get<String>("topPickId")

    private val isGuestSession = MutableStateFlow(false)

    private val _topPickView = MutableStateFlow(TopPickDetailView())
    val topPickView =
        combine(_topPickView, appPreferences.isGuestLoggedIn) { state, isGuestLoggedIn ->
            state.copy(isGuestSession = isGuestLoggedIn == true)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TopPickDetailView()
        )

    private val _actions = MutableSharedFlow<TopPickAction>()
    val actions get() = _actions

    private val _allTopPicks = MutableStateFlow(TopPicksView())
    val allTopPicks get() = _allTopPicks

    private val _savedTopPicks = MutableStateFlow(TopPicksView())
    val savedTopPicks get() = _savedTopPicks

    init {
        viewModelScope.launch {
            authenticationRepository.getAuthState().collect { isSignedIn ->
                _topPickView.update { it.copy(isLoggedIn = isSignedIn) }
            }
        }
        viewModelScope.launch {
            appPreferences.isGuestLoggedIn.collect { isGuest ->
                isGuestSession.value = isGuest == true
            }
        }
        getAllTopPicks()
        getTopPick()
    }

    fun updateTopPickId(topPickId: String) {
        savedStateHandle["topPickId"] = topPickId
        getTopPick()
    }

    private fun getTopPick() {
        _topPickView.update { it.copy(loading = true) }
        topPickId?.let { id ->
            getSingleTopPickUseCase(id) {
                it.fold(
                    ::handleGetTopPickFailure,
                    ::handleGetTopPickSuccess
                )
            }
        }
    }

    private fun handleGetTopPickFailure(failure: Failure) {
        _topPickView.update { it.copy(loading = false) }
        if (failure is Failure.RateLimitExceeded && isGuestSession.value) {
            guestRateLimitNotifier.notifyRateLimit()
        }
        Logger.e(failure.toString())
    }

    private fun handleGetTopPickSuccess(topPick: TopPick) {
        _topPickView.update {
            it.copy(
                loading = false,
                topPick = with(topPick) {
                    TopPickPresentation(
                        id = id,
                        companyName = companyName,
                        ticker = ticker,
                        rationale = rationale,
                        metrics = metrics,
                        risks = risks,
                        confidenceScore = confidenceScore,
                        isSaved = isSaved,
                        imageUrl = imageUrl,
                        percentageChange = percentageChange,
                        currentPrice = currentPrice
                    )
                }
            )
        }
        getCompanyFinancials(ticker = topPick.ticker)
        viewModelScope.launch {
            if (appPreferences.isGuestLoggedIn.first() != true) getBrief(ticker = topPick.ticker)
        }
        Logger.e(topPick.toString())
    }

    private fun getBrief(ticker: String) {
        _topPickView.update { it.copy(briefLoading = true, briefFailed = false) }
        getCompanyBriefUseCase(ticker) {
            it.fold(
                { failure ->
                    Logger.e(failure.toString())
                    _topPickView.update { state -> state.copy(briefLoading = false, briefFailed = true) }
                },
                { brief -> _topPickView.update { state -> state.copy(briefLoading = false, brief = brief) } }
            )
        }
    }

    private fun setWatched(watch: Boolean) {
        val state = _topPickView.value
        val ticker = state.brief?.ticker ?: state.topPick?.ticker ?: return
        if (state.watchlistPending) return
        _topPickView.update { it.copy(watchlistPending = true) }
        viewModelScope.launch {
            val result = if (watch) {
                watchlistRepository.addStockToWatchList(ticker)
            } else {
                watchlistRepository.removeFromWatchlist(ticker)
            }
            _topPickView.update { it.copy(watchlistPending = false) }
            result.fold(
                { failure ->
                    when {
                        watch && failure is WatchlistFailure.WatchlistFull ->
                            premiumEventBus.requestPremium(PREMIUM_SOURCE_WATCHLIST_FULL, failure.cap, ticker)

                        watch -> processAction(TopPickAction.WatchlistAddFailed)

                        else -> processAction(TopPickAction.WatchlistRemoveFailed(ticker))
                    }
                },
                {
                    analyticsLogger.logEvent(
                        eventName = if (watch) "stock-added-to-watchlist" else "stock-removed-from-watchlist",
                        params = mapOf("ticker" to ticker, "source" to "top_pick")
                    )
                    _topPickView.update { it.copy(brief = it.brief?.copy(isWatched = watch)) }
                    if (!watch) processAction(TopPickAction.WatchlistRemoved(ticker))
                }
            )
        }
    }

    fun getAllTopPicks() {
        _allTopPicks.update { it.copy(loading = true) }
        _savedTopPicks.update { it.copy(loading = true) }

        getLocalTopPicksUseCase(GetLocalTopPicksUseCase.None()) {
            it.onFailure {
                _allTopPicks.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
                _savedTopPicks.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
            }

            it.onSuccess { result ->

                val allTopPicks = result.map { topPick ->
                    with(topPick) {
                        TopPickPresentation(
                            id = id,
                            companyName = companyName,
                            ticker = ticker,
                            rationale = rationale,
                            metrics = metrics,
                            risks = risks,
                            confidenceScore = confidenceScore,
                            isSaved = isSaved,
                            imageUrl = imageUrl,
                            percentageChange = percentageChange,
                            currentPrice = currentPrice
                        )
                    }
                }

                _allTopPicks.update { state ->
                    state.copy(
                        loading = false,
                        topPicks = allTopPicks
                    )
                }

                _savedTopPicks.update { state ->
                    state.copy(
                        loading = false,
                        topPicks = allTopPicks.filter { pick -> pick.isSaved }
                    )
                }
            }
        }
    }

    fun handleSave() {
        if (_topPickView.value.topPick?.isSaved == true) {
            removeTopPickFromSaved()
        } else {
            saveTopPick()
        }
    }

    private fun saveTopPick() {
        topPickId?.let { id ->
            saveTopPickUseCase(id) {
                it.onSuccess { topPick ->
                    _topPickView.update { state ->
                        state.copy(
                            loading = false,
                            topPick = with(topPick) {
                                TopPickPresentation(
                                    id = id,
                                    companyName = companyName,
                                    ticker = ticker,
                                    rationale = rationale,
                                    metrics = metrics,
                                    risks = risks,
                                    confidenceScore = confidenceScore,
                                    isSaved = isSaved,
                                    imageUrl = imageUrl,
                                    percentageChange = percentageChange,
                                    currentPrice = currentPrice
                                )
                            }
                        )
                    }
                    processAction(TopPickAction.PickSaved)
                }

                it.onFailure { failure ->
                    Logger.e(failure.toString())
                    processAction(TopPickAction.PickSaveFailed)
                }
            }
        }
    }

    private fun removeTopPickFromSaved() {
        topPickId?.let { id ->
            removeTopPickFromSavedUseCase(id) {
                it.onSuccess { topPick ->
                    _topPickView.update { state ->
                        state.copy(
                            loading = false,
                            topPick = with(topPick) {
                                TopPickPresentation(
                                    id = id,
                                    companyName = companyName,
                                    ticker = ticker,
                                    rationale = rationale,
                                    metrics = metrics,
                                    risks = risks,
                                    confidenceScore = confidenceScore,
                                    isSaved = isSaved,
                                    imageUrl = imageUrl,
                                    percentageChange = percentageChange,
                                    currentPrice = currentPrice
                                )
                            }
                        )
                    }
                }

                it.onFailure { failure ->
                    Logger.e(failure.toString())
                }
            }
        }
    }

    fun shareTopPick() {
        topPickId?.let { id ->
            shareTopPickUseCase(id) {
                it.onSuccess { result ->
                    viewModelScope.launch {
                        _actions.emit(TopPickAction.OnShare(result))
                    }
                }
                it.onFailure {
                    Logger.e(it.toString())
                }
            }
        }
    }

    fun getCompanyFinancials(ticker: String) {
        getCompanyUseCase(ticker) {
            it.onSuccess { result ->
                _topPickView.update { state ->
                    state.copy(
                        companyPresentation = result
                    )
                }
            }
            it.onFailure {
                Logger.e(it.toString())
            }
        }
    }

    fun handleEvent(event: TopPickEvent) {
        when (event) {
            is TopPickEvent.Authenticate -> {
                _topPickView.update { it.copy(showAuthenticateDialog = event.showDialog) }
            }

            is TopPickEvent.AuthenticationResponse -> {
                Logger.e(event.message)
                viewModelScope.launch {
                    _actions.emit(ShowToast(event.message))
                }
                if (event.message.contains("success", ignoreCase = true)) {
                    _topPickView.update {
                        it.copy(
                            showAuthenticateDialog = false,
                            isLoggedIn = true
                        )
                    }
                }
            }

            is TopPickEvent.ClickNewsSources -> {
                _topPickView.update { it.copy(showNewsSourcesBottomSheet = event.show) }
            }

            is TopPickEvent.GetTopPick -> {
                updateTopPickId(event.id)
            }

            TopPickEvent.BookmarkTopPick -> {
                saveTopPick()
            }

            TopPickEvent.LikeTopPick -> logLike(liked = true)

            TopPickEvent.RemoveLikeTopPick -> logLike(liked = false)

            TopPickEvent.RemoveBookmarkTopPick -> {
                removeTopPickFromSaved()
            }

            TopPickEvent.ShareTopPick -> {
                shareTopPick()
            }

            TopPickEvent.GoToLogin -> {
                processAction(TopPickAction.OnGoToLogin)
            }

            TopPickEvent.Retry -> getTopPick()

            TopPickEvent.RetryBrief -> _topPickView.value.topPick?.ticker?.let { ticker ->
                getBrief(ticker)
                if (_topPickView.value.companyPresentation == null) getCompanyFinancials(ticker)
            }

            TopPickEvent.AddToWatchlist -> setWatched(true)

            TopPickEvent.RemoveFromWatchlist -> setWatched(false)

            is TopPickEvent.AskAboutPick -> {
                val ticker = _topPickView.value.brief?.ticker ?: _topPickView.value.topPick?.ticker
                if (ticker != null && event.query.isNotBlank()) {
                    processAction(TopPickAction.OpenCompanyAsk(ticker = ticker, query = event.query.trim()))
                }
            }
        }
    }

    private fun logLike(liked: Boolean) {
        val pick = _topPickView.value.topPick ?: return
        analyticsLogger.logEvent(
            eventName = if (liked) "top-pick-liked" else "top-pick-unliked",
            params = mapOf("top_pick_id" to pick.id, "ticker" to pick.ticker)
        )
    }

    fun processAction(action: TopPickAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

sealed interface TopPickAction {
    data class OnShare(val url: String) : TopPickAction
    data class ShowToast(val message: String) : TopPickAction
    data object OnGoBack : TopPickAction
    data object OnGoToLogin : TopPickAction
    data class OpenCompanyAsk(val ticker: String, val query: String) : TopPickAction
    data object WatchlistAddFailed : TopPickAction
    data class WatchlistRemoved(val ticker: String) : TopPickAction
    data class WatchlistRemoveFailed(val ticker: String) : TopPickAction
    data object PickSaved : TopPickAction
    data object PickSaveFailed : TopPickAction
}

sealed interface TopPickEvent {
    data class Authenticate(val showDialog: Boolean) : TopPickEvent
    data class AuthenticationResponse(val message: String) : TopPickEvent
    data class ClickNewsSources(val show: Boolean) : TopPickEvent
    data class GetTopPick(val id: String) : TopPickEvent
    data object ShareTopPick : TopPickEvent
    data object BookmarkTopPick : TopPickEvent
    data object RemoveBookmarkTopPick : TopPickEvent
    data object LikeTopPick : TopPickEvent
    data object RemoveLikeTopPick : TopPickEvent
    data object GoToLogin : TopPickEvent
    data object Retry : TopPickEvent
    data object RetryBrief : TopPickEvent
    data object AddToWatchlist : TopPickEvent
    data object RemoveFromWatchlist : TopPickEvent
    data class AskAboutPick(val query: String) : TopPickEvent
}
