package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.company.domain.model.Market
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

data class BrowseStocksState(
    val sectors: List<SectorInput> = emptyList(),
    val selectedSector: SectorInput = SectorInput.AllSector,
    val query: String = "",
    val markets: List<Market> = emptyList(),
    val selectedMarketKey: String? = null,
    val totalCompanies: Int? = null,
    val isMarketSheetOpen: Boolean = false
) {
    val showMarketFilter: Boolean get() = markets.size >= 2
    val selectedMarket: Market? get() = markets.firstOrNull { it.key == selectedMarketKey }
    val allMarketsCount: Int get() = markets.sumOf { it.count }
    val countLine: Int? get() = totalCompanies.takeIf { showMarketFilter }
}

sealed interface BrowseStocksEvent {
    data class SelectSector(val sector: SectorInput) : BrowseStocksEvent
    data object OpenMarketSheet : BrowseStocksEvent
    data object DismissMarketSheet : BrowseStocksEvent
    data class SelectMarket(val marketKey: String?) : BrowseStocksEvent
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
    private val isSavedMarketRead = MutableStateFlow(false)
    private var marketsJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val companiesPaging: Flow<PagingData<CompanyPresentation>> =
        combine(filter, isSavedMarketRead) { filter, ready -> filter.takeIf { ready } }
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { query ->
                repository.searchCompaniesPaged(query) { total ->
                    if (filter.value == query) _state.update { it.copy(totalCompanies = total) }
                }
            }
            .map { pagingData -> pagingData.map { it.toPresentation() } }
            .cachedIn(viewModelScope)

    init {
        loadSectors()
        loadInitialMarket()
    }

    private fun loadInitialMarket() {
        viewModelScope.launch {
            val savedMarket = repository.browseMarket.first()
            if (savedMarket != null) {
                _state.update { it.copy(selectedMarketKey = savedMarket) }
                filter.update { it.copy(market = savedMarket) }
            }
            isSavedMarketRead.value = true
            loadMarkets(filter.value.sector, validateSelection = true)
        }
    }

    private fun loadMarkets(sectorKey: String?, validateSelection: Boolean = false) {
        marketsJob?.cancel()
        marketsJob = viewModelScope.launch { fetchMarkets(sectorKey, validateSelection) }
    }

    private suspend fun fetchMarkets(sectorKey: String?, validateSelection: Boolean) {
        val result = repository.getMarkets(sectorKey)
        val markets = (result as? Either.Right)?.b
        val selected = _state.value.selectedMarketKey
        if (markets == null) {
            if (validateSelection && selected != null) clearMarket()
            return
        }
        _state.update { it.copy(markets = markets) }
        val isSelectedVisible = markets.size >= 2 && markets.any { it.key == selected }
        if (validateSelection && selected != null && !isSelectedVisible) {
            clearMarket()
            repository.setBrowseMarket(null)
        }
    }

    private fun clearMarket() {
        _state.update { it.copy(selectedMarketKey = null) }
        filter.update { it.copy(market = null) }
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
                loadMarkets(sectorKey)
            }

            BrowseStocksEvent.OpenMarketSheet -> {
                _state.update { it.copy(isMarketSheetOpen = true) }
                analyticsLogger.logEvent(
                    eventName = "browse-market-sheet-opened",
                    params = mapOf("market" to (_state.value.selectedMarketKey ?: "all"))
                )
            }

            BrowseStocksEvent.DismissMarketSheet -> _state.update { it.copy(isMarketSheetOpen = false) }

            is BrowseStocksEvent.SelectMarket -> {
                _state.update { it.copy(selectedMarketKey = event.marketKey, isMarketSheetOpen = false) }
                filter.update { it.copy(market = event.marketKey) }
                analyticsLogger.logEvent(
                    eventName = "browse-market-selected",
                    params = mapOf("market" to (event.marketKey ?: "all"))
                )
                viewModelScope.launch { repository.setBrowseMarket(event.marketKey) }
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
