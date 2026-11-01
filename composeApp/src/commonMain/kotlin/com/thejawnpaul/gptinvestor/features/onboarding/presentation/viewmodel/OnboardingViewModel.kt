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
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSuggestions
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingRepository
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.BriefView
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.OnboardingUiState
import kotlin.concurrent.Volatile
import kotlin.time.Clock
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
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
    private val onboardingRepository: IOnboardingRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<OnboardingAction>()
    val actions get() = _actions.asSharedFlow()

    private val appliedSearchQuery = MutableStateFlow("")

    private val onboardingStartTime = Clock.System.now().toEpochMilliseconds()
    private var briefStartTime = 0L

    private var suggestionsSource = SUGGESTIONS_SOURCE_DEFAULT
    private var suggestionsMarket: String? = DEFAULT_SUGGESTIONS_MARKET
    private var hasSelectedStock = false
    private var hasLoggedPickerViewed = false

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

    private var guestToken: Deferred<Result<Unit>> = viewModelScope.async { authRepository.acquireGuestToken() }

    @Volatile
    private var guestTokenRejected = false

    init {
        logOnboardingStarted()
        viewModelScope.launch {
            onboardingRepository.getSavedSuggestions()?.let { showSuggestions(it, SUGGESTIONS_SOURCE_SAVED) }
            if (guestToken.await().isSuccess) {
                val result = onboardingRepository.fetchSuggestions()
                result.onSuccess { showSuggestions(it, SUGGESTIONS_SOURCE_NETWORK) }
                if (result.isRight) return@launch
            }
            if (suggestionsSource == SUGGESTIONS_SOURCE_DEFAULT) loadDefaultSuggestionLogos()
        }
    }

    fun onNextScreen() {
        _uiState.update { it.copy(currentScreen = it.currentScreen + 1) }
        if (_uiState.value.currentScreen == PICK_SCREEN) logPickerViewed()
    }

    fun onPreviousScreen() {
        _uiState.update { it.copy(currentScreen = (it.currentScreen - 1).coerceAtLeast(0)) }
    }

    fun onSkip(screenIndex: Int) {
        analyticsLogger.logEvent(
            eventName = "onboarding-skipped",
            params = mapOf("screen_index" to screenIndex)
        )
        markCompletedAndNavigate(OnboardingAction.NavigateToHome)
    }

    fun onStockSelected(ticker: String, companyName: String, source: String, logoUrl: String?) {
        hasSelectedStock = true
        analyticsLogger.logEvent(
            eventName = "onboarding-stock-selected",
            params = mapOf("ticker" to ticker, "source" to source)
        )
        _uiState.update {
            it.copy(
                selectedTicker = ticker,
                selectedCompanyName = companyName,
                selectedLogoUrl = logoUrl,
                currentScreen = 3,
                briefView = BriefView.Loading
            )
        }
        briefStartTime = Clock.System.now().toEpochMilliseconds()
        fetchBrief(ticker)
    }

    fun onRetryBrief() {
        val ticker = _uiState.value.selectedTicker ?: return
        _uiState.update { it.copy(briefView = BriefView.Loading) }
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
        markCompletedAndNavigate(OnboardingAction.NavigateToSignUp)
    }

    private fun fetchBrief(ticker: String) {
        viewModelScope.launch {
            if (guestTokenRejected || guestToken.await().isFailure) {
                guestTokenRejected = false
                guestToken = async { authRepository.acquireGuestToken() }
            }
            if (guestToken.await().isFailure) {
                analyticsLogger.logEvent(
                    eventName = "onboarding-brief-failed",
                    params = mapOf("ticker" to ticker, "error_type" to "guest_token")
                )
                _uiState.update { it.copy(briefView = BriefView.Error("Something went wrong.")) }
                return@launch
            }
            requestBrief(ticker)
        }
    }

    private fun requestBrief(ticker: String) {
        getCompanyBriefUseCase(ticker) { result ->
            result.onFailure { failure ->
                val errorType = when (failure) {
                    is Failure.NetworkConnection -> "network"
                    is Failure.ServerError -> "server"
                    is Failure.GuestLimitReached -> "guest_limit"
                    is Failure.GuestSessionExpired -> "guest_session"
                    else -> "unknown"
                }
                if (failure is Failure.GuestSessionExpired) guestTokenRejected = true
                analyticsLogger.logEvent(
                    eventName = "onboarding-brief-failed",
                    params = mapOf("ticker" to ticker, "error_type" to errorType)
                )
                val briefView = if (failure is Failure.GuestLimitReached) {
                    BriefView.LimitReached
                } else {
                    BriefView.Error("Something went wrong.")
                }
                _uiState.update { it.copy(briefView = briefView) }
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

    private fun markCompletedAndNavigate(action: OnboardingAction) {
        viewModelScope.launch {
            appPreferences.setHasCompletedOnboarding(true)
            _actions.emit(action)
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

    private fun showSuggestions(suggestions: OnboardingSuggestions, source: String) {
        if (hasSelectedStock || suggestions.stocks.isEmpty()) return
        suggestionsSource = source
        suggestionsMarket = suggestions.market
        _uiState.update { it.copy(suggestedStocks = suggestions.stocks) }
    }

    private suspend fun loadDefaultSuggestionLogos() {
        val tickers = _uiState.value.suggestedStocks.map { it.ticker }
        companyRepository.getCompanyLogos(tickers).onSuccess { logos ->
            if (suggestionsSource != SUGGESTIONS_SOURCE_DEFAULT) return@onSuccess
            _uiState.update { state ->
                state.copy(
                    suggestedStocks = state.suggestedStocks.map { stock ->
                        logos[stock.ticker]?.let { stock.copy(logoUrl = it) } ?: stock
                    }
                )
            }
        }
    }

    private fun logPickerViewed() {
        if (hasLoggedPickerViewed) return
        hasLoggedPickerViewed = true
        analyticsLogger.logEvent(
            eventName = "onboarding-stock-picker-viewed",
            params = buildMap {
                put("suggestions_source", suggestionsSource)
                suggestionsMarket?.let { put("suggestions_market", it) }
            }
        )
    }
}

sealed interface OnboardingAction {
    data object NavigateToHome : OnboardingAction
    data object NavigateToSignUp : OnboardingAction
}

private const val PICK_SCREEN = 2
private const val SUGGESTIONS_SOURCE_NETWORK = "network"
private const val SUGGESTIONS_SOURCE_SAVED = "saved"
private const val SUGGESTIONS_SOURCE_DEFAULT = "default"
private const val DEFAULT_SUGGESTIONS_MARKET = "US"
