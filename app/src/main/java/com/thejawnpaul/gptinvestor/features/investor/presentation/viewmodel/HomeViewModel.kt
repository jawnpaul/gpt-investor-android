package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetTrendingCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AnotherModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction.*
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetLocalTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingCompaniesUseCase: GetTrendingCompaniesUseCase,
    private val getTopPicksUseCase: GetTopPicksUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val getLocalTopPicksUseCase: GetLocalTopPicksUseCase,
    private val remoteConfig: RemoteConfig,
    private val preferences: GPTInvestorPreferences,
    private val analyticsLogger: AnalyticsLogger,
    private val topPickRepository: ITopPickRepository,
    private val notificationRepository: NotificationRepository
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<HomeAction>()
    val actions get() = _actions

    val theme = preferences.themePreference
    val notificationPermission = preferences.notificationPermission

    init {
        remoteConfig.init()
        getTopPicks()
        getCurrentUser()

        viewModelScope.launch {
            preferences.themePreference.collect { theme ->
                _uiState.update { it.copy(theme = theme) }
            }
            preferences.notificationPermission.collect { permission ->
                _uiState.update { it.copy(requestForNotificationPermission = permission) }
            }
        }
    }

    fun getTrendingCompanies() {
        _uiState.update { currentState ->
            currentState.copy(
                trendingCompaniesView = currentState.trendingCompaniesView.copy(
                    loading = true,
                    error = null
                )
            )
        }

        getTrendingCompaniesUseCase(GetTrendingCompaniesUseCase.None()) { result ->
            result.onFailure {
                Timber.e("Something went wrong trending stocks")
                _uiState.update { currentState ->
                    currentState.copy(
                        trendingCompaniesView = currentState.trendingCompaniesView.copy(
                            loading = false,
                            error = "Something went wrong."
                        )
                    )
                }
            }

            result.onSuccess { companies ->
                _uiState.update { currentState ->
                    currentState.copy(
                        trendingCompaniesView = currentState.trendingCompaniesView.copy(
                            loading = false,
                            companies = companies.map { company ->
                                with(company) {
                                    TrendingStockPresentation(
                                        companyName = companyName,
                                        tickerSymbol = tickerSymbol,
                                        imageUrl = imageUrl,
                                        percentageChange = percentageChange.toTwoDecimalPlaces()
                                    )
                                }
                            }
                        )
                    )
                }
            }
        }
    }

    fun getTopPicks() {
        getTopPicksUseCase(GetTopPicksUseCase.None()) {
            it.onFailure {
            }
            it.onSuccess {
            }
        }

        viewModelScope.launch {
            topPickRepository.getTopPicksByDate().collect { topPicks ->
                val topPicksPresentation = topPicks.map { topPick ->
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
                _uiState.update { currentState ->
                    currentState.copy(
                        topPicksView = currentState.topPicksView.copy(
                            loading = false,
                            topPicks = topPicksPresentation
                        )
                    )
                }
            }
        }

        /*_uiState.update { currentState ->
            currentState.copy(
                topPicksView = currentState.topPicksView.copy(loading = true, error = null)
            )
        }

        getTopPicksUseCase(GetTopPicksUseCase.None()) { result ->
            result.onFailure {
                _uiState.update { currentState ->
                    currentState.copy(
                        topPicksView = currentState.topPicksView.copy(
                            loading = false,
                            error = "Something went wrong."
                        )
                    )
                }
                getLocalPicks()
            }

            result.onSuccess { topPicksResult ->
                _uiState.update { currentState ->
                    currentState.copy(
                        topPicksView = currentState.topPicksView.copy(
                            loading = false,
                            topPicks = topPicksResult.map { topPick ->
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
                        )
                    )
                }
            }
        }*/
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            authenticationRepository.getAuthState().collect { isSignedIn ->
                _uiState.update {
                    it.copy(currentUser = if (isSignedIn) authenticationRepository.currentUser else null)
                }
            }
        }
    }

    private fun getLocalPicks() {
        _uiState.update { currentState ->
            currentState.copy(
                topPicksView = currentState.topPicksView.copy(
                    loading = true,
                    error = null
                )
            )
        }

        getLocalTopPicksUseCase(GetLocalTopPicksUseCase.None()) { result ->
            result.onFailure {
                _uiState.update { currentState ->
                    currentState.copy(
                        topPicksView = currentState.topPicksView.copy(
                            loading = false,
                            error = "Something went wrong fetching local picks."
                        )
                    )
                }
            }

            result.onSuccess { localPicksResult ->
                _uiState.update { currentState ->
                    currentState.copy(
                        topPicksView = currentState.topPicksView.copy(
                            loading = false,
                            topPicks = localPicksResult.map { topPick ->
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
                        )
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
                        eventName = "Theme Changed",
                        params = mapOf("theme" to event.theme)
                    )
                }

                HomeEvent.RetryTopPicks -> {
                    getTopPicks()
                }

                HomeEvent.RetryTrendingStocks -> {
                    getTrendingCompanies()
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
                        eventName = "Notification Permission Granted",
                        params = mapOf("permission" to true)
                    )
                    // firebase token generation
                    notificationRepository.generateToken()
                }

                is HomeEvent.ModelChanged -> {
                    _uiState.update { it.copy(selectedModel = event.model) }
                }
            }
        }
    }

    fun processAction(action: HomeAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

data class HomeUiState(
    val trendingCompaniesView: TrendingCompaniesView = TrendingCompaniesView(),
    val topPicksView: TopPicksView = TopPicksView(),
    val currentUser: FirebaseUser? = null,
    val chatInput: String? = null,
    val theme: String? = "Dark",
    val requestForNotificationPermission: Boolean? = null,
    val availableModels: List<AvailableModel> = listOf(
        DefaultModel(isDefault = true),
        AnotherModel(
            isDefault = false,
            modelTitle = "Intermediate",
            modelSubtitle = "For in-depth advance use",
            canUpgrade = true,
            isUserOnWaitlist = false,
            modelId = "gemini-2.5-flash"
        )
    ),
    val selectedModel: AvailableModel = DefaultModel()
)

sealed interface HomeEvent {
    data class ChatInputChanged(val input: String) : HomeEvent
    data object SendClick : HomeEvent
    data class ChangeTheme(val theme: String) : HomeEvent
    data object RetryTrendingStocks : HomeEvent
    data object RetryTopPicks : HomeEvent
    data object NotificationPermissionGranted : HomeEvent
    data object NotificationPermissionDenied : HomeEvent
    data class ModelChanged(val model: AvailableModel) : HomeEvent
}

sealed interface HomeAction {
    data class OnStartConversation(val input: String? = null) : HomeAction
    data object OnMenuClick : HomeAction
    data object OnGoToAllTopPicks : HomeAction
    data class OnGoToTopPickDetail(val id: String) : HomeAction
    data class OnGoToCompanyDetail(val ticker: String) : HomeAction
    data object OnGoToDiscover : HomeAction
}
