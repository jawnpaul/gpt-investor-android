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
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction.*
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetLocalTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val analyticsLogger: AnalyticsLogger
) :
    ViewModel() {

    private val _trendingCompanies = MutableStateFlow(TrendingCompaniesView())
    val trendingCompanies get() = _trendingCompanies

    private val _topPicks = MutableStateFlow(TopPicksView())
    val topPicks get() = _topPicks

    private val _allTopPicks = MutableStateFlow(TopPicksView())
    val allTopPicks get() = _allTopPicks

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser get() = _currentUser

    private val _homeState = MutableStateFlow(HomeState())
    val homeState get() = _homeState

    private val _actions = MutableSharedFlow<HomeAction>()
    val actions get() = _actions

    val theme = preferences.themePreference

    init {
        getTrendingCompanies()
        remoteConfig.init()
        getTopPicks()
        getCurrentUser()
    }

    fun getTrendingCompanies() {
        _trendingCompanies.update { it.copy(loading = true) }

        getTrendingCompaniesUseCase(GetTrendingCompaniesUseCase.None()) {
            it.onFailure {
                Timber.e("Something went wrong trending stocks")
                _trendingCompanies.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
            }

            it.onSuccess { result ->
                _trendingCompanies.update { state ->
                    state.copy(
                        loading = false,
                        companies = result.map { company ->
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
                }
            }
        }
    }

    fun getTopPicks() {
        _topPicks.update { it.copy(loading = true) }

        getTopPicksUseCase(GetTopPicksUseCase.None()) {
            it.onFailure {
                _topPicks.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
                getLocalPicks()
            }

            it.onSuccess { result ->
                _topPicks.update { state ->
                    state.copy(
                        loading = false,
                        topPicks = result.map { topPick ->
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
                }
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            authenticationRepository.getAuthState().collect { isSignedIn ->
                _currentUser.update { if (isSignedIn) authenticationRepository.currentUser else null }
            }
        }
    }

    private fun getLocalPicks() {
        getLocalTopPicksUseCase(GetLocalTopPicksUseCase.None()) {
            it.onFailure {
                _topPicks.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
            }

            it.onSuccess { result ->
                _topPicks.update { state ->
                    state.copy(
                        loading = false,
                        topPicks = result.map { topPick ->
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
                }
            }
        }
    }

    fun handleEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeEvent.ChatInputChanged -> {
                    _homeState.update { it.copy(chatInput = event.input) }
                }

                is HomeEvent.SendClick -> {
                    _actions.emit(OnSendClick(_homeState.value.chatInput))
                    _homeState.update { it.copy(chatInput = null) }
                }

                is HomeEvent.ChangeTheme -> {
                    preferences.setThemePreference(event.theme)
                    analyticsLogger.logEvent(
                        eventName = "Theme Changed",
                        params = mapOf("theme" to event.theme)
                    )
                }
            }
        }
    }
}

data class HomeState(
    val chatInput: String? = null
)

sealed interface HomeEvent {
    data class ChatInputChanged(val input: String) : HomeEvent
    data object SendClick : HomeEvent
    data class ChangeTheme(val theme: String) : HomeEvent
}

sealed interface HomeAction {
    data class OnSendClick(val input: String? = null) : HomeAction
}
