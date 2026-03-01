package com.thejawnpaul.gptinvestor.features.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import com.thejawnpaul.gptinvestor.features.toppick.data.repository.TopPickRepository
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val companyRepository: CompanyRepository,
    private val topPickRepository: TopPickRepository
) : ViewModel() {

    private val _discoveryScreenState = MutableStateFlow(DiscoveryScreenState())
    val discoveryScreenState get() = _discoveryScreenState

    private val _actions = MutableSharedFlow<DiscoveryAction>()
    val actions get() = _actions.asSharedFlow()

    private val _appliedSearchQuery = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        getAllSector()
        getTopPicks()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val companiesPagingData: Flow<PagingData<CompanyPresentation>> =
        combine(
            _discoveryScreenState.map { it.selected }.distinctUntilChanged(),
            _appliedSearchQuery
        ) { selectedSector, query ->
            selectedSector to query
        }.flatMapLatest { (selectedSector, query) ->
            val sectorKey = when (selectedSector) {
                is SectorInput.CustomSector -> if (selectedSector.sectorKey == "top-picks") null else selectedSector.sectorKey
                else -> null
            }

            companyRepository.searchCompaniesPaged(
                query = SearchCompanyQuery(
                    sector = sectorKey,
                    query = query
                )
            ).map { pagingData ->
                pagingData.map { company -> mapCompanyToPresentation(company) }
            }
        }.cachedIn(viewModelScope)

    private fun mapCompanyToPresentation(company: Company): CompanyPresentation {
        return CompanyPresentation(
            ticker = company.ticker,
            name = company.name,
            logo = company.logo,
            price = company.price ?: 0.0f,
            summary = company.summary,
            priceChange = PriceChange(change = 0f, date = 1L)

        )
    }

    fun handleEvent(event: DiscoveryEvent) {
        when (event) {
            DiscoveryEvent.GoBack -> {
                processAction(DiscoveryAction.OnGoBack)
            }

            is DiscoveryEvent.GoToCompanyDetail -> {
                processAction(DiscoveryAction.OnNavigateToCompanyDetail(ticker = event.ticker))
            }

            is DiscoveryEvent.GoToTopPickDetail -> {
                processAction(DiscoveryAction.OnGoToPickDetail(id = event.id))
            }

            DiscoveryEvent.PerformSearch -> {
                searchCompanies(_discoveryScreenState.value.query, immediate = true)
            }

            DiscoveryEvent.RetryCompanies -> {

            }

            is DiscoveryEvent.SearchQueryChanged -> {
                _discoveryScreenState.update { currentState ->
                    currentState.copy(query = event.query)
                }
                searchCompanies(event.query)
            }

            is DiscoveryEvent.SelectSector -> {
                handleSectorChange(event.sector)
            }

            is DiscoveryEvent.ToggleSearchMode -> {
                _discoveryScreenState.update { currentState ->
                    currentState.copy(searchMode = event.searchMode)
                }
                if (!event.searchMode) {
                    _appliedSearchQuery.value = ""
                    _discoveryScreenState.update { currentState ->
                        currentState.copy(query = "")
                    }
                }
            }
        }
    }

    private fun getAllSector() {
        viewModelScope.launch {
            companyRepository.getAllSector().collect { result ->
                result.onSuccess { sectors ->
                    _discoveryScreenState.update { currentState ->
                        currentState.copy(
                            sectors = sectors,
                            selected = currentState.selected ?: sectors.firstOrNull()
                        )
                    }
                }
                result.onFailure {

                }
            }
        }
    }

    private fun getTopPicks() {
        viewModelScope.launch {
            topPickRepository.getTopPicks().collect { result ->
                result.onSuccess { topPicks ->
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
                    _discoveryScreenState.update { currentState -> currentState.copy(topPicks = topPicksPresentation) }
                }

            }
        }
    }

    private fun handleSectorChange(sector: SectorInput) {
        _discoveryScreenState.update { currentState ->
            currentState.copy(selected = sector)
        }
    }

    private fun processAction(action: DiscoveryAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }

    private fun searchCompanies(query: String, immediate: Boolean = false) {
        searchJob?.cancel()
        if (immediate || query.isEmpty()) {
            _appliedSearchQuery.value = query
        } else {
            searchJob = viewModelScope.launch {
                delay(500L)
                _appliedSearchQuery.value = query
            }
        }
    }
}

data class DiscoveryScreenState(
    val searchMode: Boolean = false,
    val topPicks: List<TopPickPresentation> = emptyList(),
    val sectors: List<SectorInput> = emptyList(),
    val query: String = "",
    val selected: SectorInput? = null
) {
    val showTopPicks = selected is SectorInput.CustomSector && selected.sectorKey == "top-picks"
}

sealed interface DiscoveryEvent {
    data class SearchQueryChanged(val query: String) : DiscoveryEvent
    data object PerformSearch : DiscoveryEvent
    data class SelectSector(val sector: SectorInput) : DiscoveryEvent
    data object RetryCompanies : DiscoveryEvent
    data object GoBack : DiscoveryEvent
    data class ToggleSearchMode(val searchMode: Boolean) : DiscoveryEvent
    data class GoToTopPickDetail(val id: String) : DiscoveryEvent
    data class GoToCompanyDetail(val ticker: String) : DiscoveryEvent
}

sealed interface DiscoveryAction {
    data class OnNavigateToCompanyDetail(val ticker: String) : DiscoveryAction
    data object OnGoBack : DiscoveryAction
    data class OnGoToPickDetail(val id: String) : DiscoveryAction

}