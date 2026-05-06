package com.thejawnpaul.gptinvestor.features.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyBriefUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.BriefView
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.OnboardingUiState
import kotlin.time.Clock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class OnboardingViewModel(
    private val getCompanyBriefUseCase: GetCompanyBriefUseCase,
    private val companyRepository: CompanyRepository,
    private val appPreferences: AppPreferences,
    private val authRepository: AuthenticationRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<OnboardingAction>()
    val actions get() = _actions.asSharedFlow()

    private val appliedSearchQuery = MutableStateFlow("")

    private val onboardingStartTime = Clock.System.now().toEpochMilliseconds()
    private var briefStartTime = 0L

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<CompanyPresentation>> = appliedSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                companyRepository.searchCompaniesPaged(SearchCompanyQuery(query = query))
                    .map { pagingData -> pagingData.map { company -> company.toPresentation() } }
            }
        }.cachedIn(viewModelScope)

    init {
        logOnboardingStarted()
        viewModelScope.launch {
            authRepository.acquireGuestToken()
            loadSuggestedStockLogos()
        }
    }

    fun onNextScreen() {
        _uiState.update { it.copy(currentScreen = it.currentScreen + 1) }
    }

    fun onSkip(screenIndex: Int) {
        analyticsLogger.logEvent(
            eventName = "onboarding-skipped",
            params = mapOf("screen_index" to screenIndex)
        )
        markCompletedAndNavigateHome()
    }

    fun onStockSelected(ticker: String, companyName: String, source: String) {
        analyticsLogger.logEvent(
            eventName = "onboarding-stock-selected",
            params = mapOf("ticker" to ticker, "source" to source)
        )
        _uiState.update {
            it.copy(
                selectedTicker = ticker,
                selectedCompanyName = companyName,
                currentScreen = 3,
                briefView = BriefView.Loading
            )
        }
        briefStartTime = Clock.System.now().toEpochMilliseconds()
        fetchBrief(ticker)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        appliedSearchQuery.value = query
    }

    fun onBackToStockSelection() {
        _uiState.update { it.copy(currentScreen = 2) }
    }

    fun onFinish() {
        val ticker = _uiState.value.selectedTicker.orEmpty()
        val durationMs = Clock.System.now().toEpochMilliseconds() - onboardingStartTime
        analyticsLogger.logEvent(
            eventName = "onboarding-finished",
            params = mapOf("ticker" to ticker, "total_duration_ms" to durationMs)
        )
        markCompletedAndNavigateHome()
    }

    private fun fetchBrief(ticker: String) {
        getCompanyBriefUseCase(ticker) { result ->
            result.onFailure { failure ->
                val errorType = when (failure) {
                    is Failure.NetworkConnection -> "network"
                    is Failure.ServerError -> "server"
                    else -> "unknown"
                }
                analyticsLogger.logEvent(
                    eventName = "onboarding-brief-failed",
                    params = mapOf("ticker" to ticker, "error_type" to errorType)
                )
                _uiState.update { it.copy(briefView = BriefView.Error("Something went wrong.")) }
            }
            result.onSuccess { brief ->
                val durationMs = Clock.System.now().toEpochMilliseconds() - briefStartTime
                analyticsLogger.logEvent(
                    eventName = "onboarding-brief-completed",
                    params = mapOf("ticker" to ticker, "duration_ms" to durationMs)
                )
                _uiState.update { it.copy(briefView = BriefView.Success(brief)) }
            }
        }
    }

    private fun markCompletedAndNavigateHome() {
        viewModelScope.launch {
            appPreferences.setHasCompletedOnboarding(true)
            _actions.emit(OnboardingAction.NavigateToHome)
        }
    }

    private fun logOnboardingStarted() {
        viewModelScope.launch {
            val isRegistered = appPreferences.isUserLoggedIn.first()
            val userType = if (isRegistered == true) "registered" else "guest"
            analyticsLogger.logEvent(
                eventName = "onboarding-started",
                params = mapOf("user_type" to userType)
            )
        }
    }

    private suspend fun loadSuggestedStockLogos() {
        val tickers = listOf("AAPL", "TSLA", "NVDA", "MSFT", "AMZN", "GOOGL")
        companyRepository.getCompanyLogos(tickers).onSuccess { logos ->
            _uiState.update { state ->
                state.copy(
                    suggestedStocks = state.suggestedStocks.map { stock ->
                        val logoUrl = logos[stock.ticker]
                        if (logoUrl != null) stock.copy(logoUrl = logoUrl) else stock
                    }
                )
            }
        }
    }
}

sealed interface OnboardingAction {
    data object NavigateToHome : OnboardingAction
}
