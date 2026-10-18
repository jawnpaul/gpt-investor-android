package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

data class BrowseStocksState(
    val sectors: List<SectorInput> = emptyList(),
    val selectedSector: SectorInput = SectorInput.AllSector,
    val query: String = ""
)

sealed interface BrowseStocksEvent {
    data class SelectSector(val sector: SectorInput) : BrowseStocksEvent
    data class UpdateQuery(val query: String) : BrowseStocksEvent
    data class GoToCompanyDetail(val ticker: String) : BrowseStocksEvent
    data object GoBack : BrowseStocksEvent
}

sealed interface BrowseStocksAction {
    data class NavigateToCompanyDetail(val ticker: String) : BrowseStocksAction
    data object GoBack : BrowseStocksAction
}

@KoinViewModel
class BrowseStocksViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ICompanyRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _state = MutableStateFlow(BrowseStocksState())
    val state = _state.asStateFlow()

    private val _actions = MutableSharedFlow<BrowseStocksAction>()
    val actions = _actions.asSharedFlow()

    private val filter = MutableStateFlow(
        SearchCompanyQuery(sector = savedStateHandle.get<String>("sector"), query = "")
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val companiesPaging: Flow<PagingData<CompanyPresentation>> =
        filter.flatMapLatest { filter ->
            repository.searchCompaniesPaged(filter)
                .map { pagingData -> pagingData.map { it.toPresentation() } }
                .cachedIn(viewModelScope)
        }

    init {
        loadSectors()
    }

    private fun loadSectors() {
        viewModelScope.launch {
            repository.getAllSector().collect { result ->
                result.onSuccess { sectors ->
                    val initialSectorKey = savedStateHandle.get<String>("sector")
                    val selected = if (initialSectorKey != null) {
                        sectors.filterIsInstance<SectorInput.CustomSector>()
                            .firstOrNull { it.sectorKey == initialSectorKey }
                            ?: SectorInput.AllSector
                    } else {
                        SectorInput.AllSector
                    }
                    _state.update { it.copy(sectors = sectors, selectedSector = selected) }
                }
            }
        }
    }

    fun handleEvent(event: BrowseStocksEvent) {
        when (event) {
            is BrowseStocksEvent.SelectSector -> {
                val sectorKey = (event.sector as? SectorInput.CustomSector)?.sectorKey
                _state.update { it.copy(selectedSector = event.sector) }
                filter.update { it.copy(sector = sectorKey) }
                analyticsLogger.logEvent(
                    eventName = "browse-sector-selected",
                    params = mapOf("sector" to (sectorKey ?: "all"))
                )
            }

            is BrowseStocksEvent.UpdateQuery -> {
                _state.update { it.copy(query = event.query) }
                filter.update { it.copy(query = event.query) }
            }

            is BrowseStocksEvent.GoToCompanyDetail -> {
                analyticsLogger.logEvent(
                    eventName = "browse-company-tapped",
                    params = mapOf("ticker" to event.ticker)
                )
                viewModelScope.launch {
                    _actions.emit(BrowseStocksAction.NavigateToCompanyDetail(event.ticker))
                }
            }

            BrowseStocksEvent.GoBack -> {
                viewModelScope.launch { _actions.emit(BrowseStocksAction.GoBack) }
            }
        }
    }
}
