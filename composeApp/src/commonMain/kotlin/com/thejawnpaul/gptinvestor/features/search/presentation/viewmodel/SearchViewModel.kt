package com.thejawnpaul.gptinvestor.features.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.search.domain.repository.ISearchRepository
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchAction
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchEvent
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class SearchViewModel(
    private val searchRepository: ISearchRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    private val _actions = MutableSharedFlow<SearchAction>()
    val actions get() = _actions.asSharedFlow()

    private var searchJob: Job? = null

    init {
        loadInitialState()
    }

    fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                _uiState.update { it.copy(query = event.query) }
                scheduleSearch(event.query)
            }
            is SearchEvent.OnStockClick -> {
                analyticsLogger.logEvent("search-stock-tapped", mapOf("ticker" to event.ticker))
                emitAction(SearchAction.OnNavigateToCompany(event.ticker))
            }
            is SearchEvent.OnSectorClick -> {
                analyticsLogger.logEvent("search-sector-tapped", mapOf("sector_key" to event.key))
                emitAction(SearchAction.OnNavigateToSector(event.key))
            }
            is SearchEvent.OnPromptClick -> {
                analyticsLogger.logEvent("search-prompt-tapped", mapOf("query" to event.query))
                emitAction(SearchAction.OnNavigateToConversation(event.query))
            }
            is SearchEvent.OnAskGptClick -> {
                analyticsLogger.logEvent("search-ask-gpt-tapped", mapOf("query" to event.query))
                emitAction(SearchAction.OnNavigateToConversation(event.query))
            }
            SearchEvent.OnClearHistory -> {
                analyticsLogger.logEvent("search-history-cleared", emptyMap())
                clearHistory()
            }
            SearchEvent.OnRetry -> scheduleSearch(_uiState.value.query)
            SearchEvent.OnBack -> emitAction(SearchAction.OnGoBack)
        }
    }

    private fun scheduleSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotBlank()) delay(500L)
            performSearch(query.ifBlank { null })
        }
    }

    private fun loadInitialState() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch { performSearch(null) }
    }

    private suspend fun performSearch(query: String?) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        if (!query.isNullOrBlank()) {
            analyticsLogger.logEvent("search-query-submitted", mapOf("query" to query))
        }
        searchRepository.search(query).collect { result ->
            result.onSuccess { sections ->
                _uiState.update { it.copy(isLoading = false, sections = sections) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = "Something went wrong. Please try again.") }
            }
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            searchRepository.clearHistory().collect { result ->
                result.onSuccess { loadInitialState() }
            }
        }
    }

    private fun emitAction(action: SearchAction) {
        viewModelScope.launch { _actions.emit(action) }
    }
}
