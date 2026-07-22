package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfigClient
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.User
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DrawerState
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetTrendingCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.digest.domain.DigestRepository
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.DailyDigestStatus
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.DailyDigestView
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.StockDigestPresentation
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction.OnStartConversation
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistFailure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class HomeViewModel(
    private val getTopPicksUseCase: GetTopPicksUseCase,
    private val getTrendingCompaniesUseCase: GetTrendingCompaniesUseCase,
    private val authenticationRepository: AuthenticationRepository,
    remoteConfig: RemoteConfigClient,
    private val preferences: AppPreferences,
    @Provided private val analyticsLogger: AnalyticsLogger,
    private val notificationRepository: NotificationRepository,
    private val modelsRepository: ModelsRepository,
    private val digestRepository: DigestRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(timePeriod = computeTimePeriod()))
    val uiState = combine(
        _uiState.asStateFlow(),
        preferences.userName,
        preferences.isGuestLoggedIn
    ) { state, userName, isGuestLoggedIn ->
        state.copy(
            drawerState = state.drawerState.copy(user = userName),
            isGuestSession = isGuestLoggedIn == true,
            firstName = extractFirstName(userName)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    private val _actions = MutableSharedFlow<HomeAction>()
    val actions get() = _actions

    val theme = preferences.themePreference
    val notificationPermission = preferences.notificationPermission

    private var upgradeModelId: String? = null

    init {
        remoteConfig.init()
        getTopPicks()
        getTrendingCompanies()
        getCurrentUser()
        logHomeScreenViewed()
        getDailyDigest()

        viewModelScope.launch {
            preferences.themePreference.collect { theme ->
                _uiState.update {
                    it.copy(
                        theme = theme,
                        drawerState = it.drawerState.copy(theme = theme)
                    )
                }
            }
            preferences.notificationPermission.collect { permission ->
                _uiState.update { it.copy(requestForNotificationPermission = permission) }
            }
        }
    }

    private fun getTopPicks() {
        _uiState.update {
            it.copy(
                topPicksView = it.topPicksView.copy(
                    loading = true,
                    error = null
                )
            )
        }
        getTopPicksUseCase(GetTopPicksUseCase.None()) { result ->
            result.onFailure {
                _uiState.update { state ->
                    state.copy(
                        topPicksView = state.topPicksView.copy(
                            loading = false,
                            error = "Something went wrong"
                        )
                    )
                }
            }
            result.onSuccess { picks ->
                _uiState.update { state ->
                    state.copy(
                        topPicksView = state.topPicksView.copy(
                            loading = false,
                            error = null,
                            topPicks = picks.map { pick ->
                                TopPickPresentation(
                                    id = pick.id,
                                    companyName = pick.companyName,
                                    ticker = pick.ticker,
                                    rationale = pick.rationale,
                                    metrics = pick.metrics,
                                    risks = pick.risks,
                                    confidenceScore = pick.confidenceScore,
                                    isSaved = pick.isSaved,
                                    percentageChange = pick.percentageChange,
                                    imageUrl = pick.imageUrl,
                                    currentPrice = pick.currentPrice
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    private fun getTrendingCompanies() {
        _uiState.update {
            it.copy(
                trendingCompaniesView = it.trendingCompaniesView.copy(
                    loading = true,
                    error = null
                )
            )
        }
        getTrendingCompaniesUseCase(GetTrendingCompaniesUseCase.None()) { result ->
            result.onFailure {
                _uiState.update { state ->
                    state.copy(
                        trendingCompaniesView = state.trendingCompaniesView.copy(
                            loading = false,
                            error = "Something went wrong"
                        )
                    )
                }
            }
            result.onSuccess { companies ->
                _uiState.update { state ->
                    state.copy(
                        trendingCompaniesView = state.trendingCompaniesView.copy(
                            loading = false,
                            error = null,
                            companies = companies.map { company ->
                                TrendingStockPresentation(
                                    companyName = company.companyName,
                                    tickerSymbol = company.tickerSymbol,
                                    imageUrl = company.imageUrl,
                                    percentageChange = company.percentageChange.toTwoDecimalPlaces()
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            authenticationRepository.getAuthState().collect { isSignedIn ->
                _uiState.update {
                    it.copy(
                        currentUser = if (isSignedIn) authenticationRepository.currentUser else null
                    )
                }
            }
        }
    }

    fun handleEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeEvent.ChatInputChanged -> {
                    _uiState.update { it.copy(chatInput = event.input) }
                }

                is HomeEvent.SendClick -> {
                    _actions.emit(OnStartConversation(_uiState.value.chatInput))
                    _uiState.update { it.copy(chatInput = null) }
                }

                is HomeEvent.ChangeTheme -> {
                    preferences.setThemePreference(event.theme)
                    analyticsLogger.logEvent(
                        eventName = "theme-changed",
                        params = mapOf("theme" to event.theme)
                    )
                }

                HomeEvent.NotificationPermissionDenied -> {
                    preferences.setNotificationPermission(false)
                }

                HomeEvent.NotificationPermissionGranted -> {
                    if (notificationPermission.first() == true) {
                        return@launch
                    }
                    preferences.setNotificationPermission(true)
                    analyticsLogger.logEvent(
                        eventName = "notification-permission-granted",
                        params = mapOf("permission" to true)
                    )
                    // firebase token generation
                    notificationRepository.syncTokenIfNeeded()
                }

                is HomeEvent.ModelChanged -> {
                    if (event.model.isUserOnWaitlist == true) {
                        return@launch
                    }

                    _uiState.update { it.copy(selectedModel = event.model) }
                    analyticsLogger.logEvent(
                        eventName = "model-changed",
                        params = mapOf("model" to event.model.modelId)
                    )
                }

                is HomeEvent.UpgradeModel -> {
                    _uiState.update { it.copy(showWaitlistBottomSheet = event.showBottomSheet) }
                    event.modelId?.let {
                        upgradeModelId = it
                    }
                }

                is HomeEvent.SelectWaitListOption -> {
                    selectWaitListOption(event.option)
                }

                HomeEvent.JoinWaitlist -> {
                    upgradeModelId?.let { modelId ->
                        joinModelWaitlist(modelId = modelId)
                    }
                }

                is HomeEvent.DefaultPromptClicked -> {
                    analyticsLogger.logEvent(
                        eventName = "default-prompt-clicked",
                        params = mapOf(
                            "prompt_title" to event.prompt.title,
                            "source" to "home_screen"
                        )
                    )
                    _actions.emit(
                        OnStartConversation(
                            input = event.prompt.query,
                            title = event.prompt.title
                        )
                    )
                    _uiState.update { it.copy(chatInput = null) }
                }

                HomeEvent.SignOut -> {
                    viewModelScope.launch {
                        authenticationRepository.signOut()
                    }
                }

                HomeEvent.GoToSignUp -> {
                    _actions.emit(HomeAction.OnGoToSignUp)
                }

                HomeEvent.RetryTrendingCompanies -> getTrendingCompanies()

                HomeEvent.GoToAllTrending -> _actions.emit(HomeAction.NavigateToAllTrending)

                is HomeEvent.ClickTrendingCompany -> {
                    analyticsLogger.logEvent(
                        eventName = "trending-stock-tapped",
                        params = mapOf(
                            "ticker" to event.ticker,
                            "user_type" to if (uiState.value.isGuestSession) "guest" else "authenticated"
                        )
                    )
                    _actions.emit(HomeAction.OnGoToCompanyDetail(event.ticker))
                }

                is HomeEvent.AddStockToDigest -> {
                    _uiState.update { state ->
                        state.copy(
                            dailyDigestView = state.dailyDigestView.copy(
                                addingTickers = state.dailyDigestView.addingTickers + event.ticker
                            )
                        )
                    }
                    viewModelScope.launch {
                        watchlistRepository.addStockToWatchList(event.ticker).onSuccess {
                            _uiState.update { state ->
                                state.copy(
                                    dailyDigestView = state.dailyDigestView.copy(
                                        addingTickers = state.dailyDigestView.addingTickers - event.ticker,
                                        addedTickers = state.dailyDigestView.addedTickers + event.ticker
                                    )
                                )
                            }
                            getDailyDigest()
                            _uiState.update { state ->
                                state.copy(
                                    dailyDigestView = state.dailyDigestView.copy(
                                        addedTickers = state.dailyDigestView.addedTickers - event.ticker
                                    )
                                )
                            }
                        }.onFailure { failure ->
                            _uiState.update { state ->
                                state.copy(
                                    dailyDigestView = state.dailyDigestView.copy(
                                        addingTickers = state.dailyDigestView.addingTickers - event.ticker
                                    )
                                )
                            }
                            when (failure) {
                                is WatchlistFailure.SignUpRequired -> {
                                    processAction(HomeAction.OnGoToSignUp)
                                }

                                is WatchlistFailure.WatchlistFull -> {
                                    processAction(HomeAction.ShowToast("Watchlist full. Upgrade to Premium!"))
                                }

                                is WatchlistFailure.TickerNotFound -> {
                                    processAction(HomeAction.ShowToast("Ticker not found"))
                                }

                                is WatchlistFailure.GeneralError -> {
                                    processAction(HomeAction.ShowToast(failure.message))
                                }

                                else -> {
                                    processAction(HomeAction.ShowToast("Failed to add to watchlist"))
                                }
                            }
                        }
                    }
                }

                HomeEvent.BrowseAllCompanies -> {
                    _actions.emit(HomeAction.NavigateToDiscover)
                }

                HomeEvent.UnlockPremium -> {
                    _actions.emit(HomeAction.NavigateToProfile)
                }

                HomeEvent.SeeFullDigest -> {
                    _actions.emit(HomeAction.NavigateToDigestDetail)
                }
            }
        }
    }

    fun processAction(action: HomeAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }

    private fun selectWaitListOption(option: String) {
        if (_uiState.value.selectedWaitlistOptions.contains(option)) {
            _uiState.update {
                it.copy(selectedWaitlistOptions = it.selectedWaitlistOptions - option)
            }
        } else {
            _uiState.update {
                it.copy(selectedWaitlistOptions = it.selectedWaitlistOptions + option)
            }
        }
    }

    private fun getAvailableModels() {
        viewModelScope.launch {
            modelsRepository.getAvailableModels().onSuccess { models ->
                _uiState.update { it.copy(availableModels = models) }
            }
        }
    }

    private fun joinModelWaitlist(modelId: String) {
        viewModelScope.launch {
            modelsRepository.putUserOnModelWaitlist(
                modelId = modelId,
                reasons = _uiState.value.selectedWaitlistOptions
            ).onSuccess {
                getAvailableModels()
            }.onFailure { failure ->
                Logger.e(failure.stackTraceToString())
            }
        }
    }

    private fun logHomeScreenViewed() {
        viewModelScope.launch {
            val isGuest = preferences.isGuestLoggedIn.first() == true
            val isFirstInstall = preferences.isFirstInstall.first() == true
            analyticsLogger.logEvent(
                eventName = "home-screen-viewed",
                params = mapOf(
                    "user_type" to if (isGuest) "guest" else "authenticated",
                    "is_returning_user" to !isFirstInstall
                )
            )
        }
    }

    private fun getDailyDigest() {
        _uiState.update { state ->
            state.copy(dailyDigestView = state.dailyDigestView.copy(loading = true))
        }
        viewModelScope.launch {
            digestRepository.getDailyDigest().onSuccess { digest ->
                val unlockedItems = digest.items?.filter { !it.locked } ?: emptyList()
                val lockedItems = digest.items?.filter { it.locked } ?: emptyList()

                val digestStatus = when {
                    digest.status?.lowercase() == "pending" -> DailyDigestStatus.PENDING
                    digest.items.isNullOrEmpty() -> DailyDigestStatus.EMPTY
                    else -> DailyDigestStatus.READY
                }

                val nextHourLabel = if (digestStatus == DailyDigestStatus.PENDING) {
                    val currentHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
                    computeNextHourLabel(currentHour)
                } else {
                    ""
                }

                _uiState.update { state ->
                    state.copy(
                        dailyDigestView = state.dailyDigestView.copy(
                            loading = false,
                            status = digestStatus,
                            nextHourLabel = nextHourLabel,
                            stocks = unlockedItems.map { item ->
                                StockDigestPresentation(
                                    ticker = item.ticker,
                                    name = item.companyName,
                                    summary = item.summary ?: "",
                                    isImproved = item.sentimentChange.lowercase() == "improved",
                                    keyEvent = item.keyEvent
                                )
                            },
                            suggestedStocks = digest.recommendations ?: emptyList(),
                            isStale = digest.isStale == true,
                            lastUpdated = digest.digestDate,
                            lockedStocksCount = lockedItems.size,
                            lockedStocksSummary = lockedItems.joinToString(" · ") { it.ticker }
                        )
                    )
                }
            }.onFailure { failure ->
                Logger.e(failure.stackTraceToString())
                _uiState.update { state ->
                    state.copy(
                        dailyDigestView = state.dailyDigestView.copy(
                            loading = false,
                            status = DailyDigestStatus.EMPTY
                        )
                    )
                }
            }
        }
    }
}

enum class TimePeriod { MORNING, AFTERNOON, EVENING }

internal fun extractFirstName(userName: String?): String? =
    userName?.trim()?.split("\\s+".toRegex())?.firstOrNull()?.takeIf { it.isNotBlank() }

internal fun computeNextHourLabel(currentHour: Int): String {
    val nextHour = (currentHour + 1) % 24
    val period = if (nextHour < 12) "AM" else "PM"
    val displayHour = when {
        nextHour == 0 -> 12
        nextHour > 12 -> nextHour - 12
        else -> nextHour
    }
    return "$displayHour $period"
}

private fun computeTimePeriod(): TimePeriod {
    val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    return when {
        hour < 12 -> TimePeriod.MORNING
        hour < 17 -> TimePeriod.AFTERNOON
        else -> TimePeriod.EVENING
    }
}

data class HomeUiState(
    val trendingCompaniesView: TrendingCompaniesView = TrendingCompaniesView(),
    val topPicksView: TopPicksView = TopPicksView(),
    val currentUser: User? = null,
    val chatInput: String? = null,
    val theme: String? = "Dark",
    val requestForNotificationPermission: Boolean? = null,
    val availableModels: List<AvailableModel> = emptyList(),
    val selectedModel: AvailableModel = DefaultModel(),
    val showWaitlistBottomSheet: Boolean = false,
    val waitlistAvailableOptions: List<String> = listOf(
        "Advanced analysis",
        "Personalized insights",
        "Risk awareness",
        "Actionable guidance",
        "Transparent data sources",
        "Unlimited queries"
    ),
    val selectedWaitlistOptions: List<String> = emptyList(),
    val defaultPrompts: List<DefaultPrompt> = emptyList(),
    val drawerState: DrawerState = DrawerState(),
    val homeTidbitView: HomeTidbitView = HomeTidbitView(),
    val isGuestSession: Boolean = false,
    val timePeriod: TimePeriod = TimePeriod.MORNING,
    val firstName: String? = null,
    val dailyDigestView: DailyDigestView = DailyDigestView(loading = true)
)

sealed interface HomeEvent {
    data class ChatInputChanged(val input: String) : HomeEvent
    data object SendClick : HomeEvent
    data class ChangeTheme(val theme: String) : HomeEvent
    data object NotificationPermissionGranted : HomeEvent
    data object NotificationPermissionDenied : HomeEvent
    data class ModelChanged(val model: AvailableModel) : HomeEvent
    data class UpgradeModel(val showBottomSheet: Boolean, val modelId: String? = null) : HomeEvent
    data class SelectWaitListOption(val option: String) : HomeEvent
    data object JoinWaitlist : HomeEvent
    data class DefaultPromptClicked(val prompt: DefaultPrompt) : HomeEvent
    data object SignOut : HomeEvent
    data object GoToSignUp : HomeEvent
    data object RetryTrendingCompanies : HomeEvent
    data object GoToAllTrending : HomeEvent
    data class ClickTrendingCompany(val ticker: String) : HomeEvent
    data class AddStockToDigest(val ticker: String) : HomeEvent
    data object BrowseAllCompanies : HomeEvent
    data object UnlockPremium : HomeEvent
    data object SeeFullDigest : HomeEvent
}

sealed interface HomeAction {
    data class OnStartConversation(val input: String? = null, val title: String? = null) : HomeAction

    data class OnGoToCompanyDetail(val ticker: String) : HomeAction
    data class ShowToast(val message: String) : HomeAction
    data object OnGoToSignUp : HomeAction
    data object NavigateToAllTrending : HomeAction
    data object NavigateToDiscover : HomeAction
    data object NavigateToProfile : HomeAction
    data object NavigateToDigestDetail : HomeAction
}
