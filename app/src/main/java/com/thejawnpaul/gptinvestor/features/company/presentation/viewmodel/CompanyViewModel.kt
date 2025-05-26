package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetAllCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetAllSectorUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyDetailInputResponseUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetSectorCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.SearchCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.state.AllCompanyView
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyFinancialsView
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyHeaderPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyDetailDefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.AllSectorView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val getAllSectorUseCase: GetAllSectorUseCase,
    private val getAllCompaniesUseCase: GetAllCompaniesUseCase,
    private val getSectorCompaniesUseCase: GetSectorCompaniesUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val companyDetailInputResponseUseCase: GetCompanyDetailInputResponseUseCase,
    private val searchCompaniesUseCase: SearchCompaniesUseCase

) : ViewModel() {

    private val _companyDiscoveryState = MutableStateFlow(CompanyDiscoveryState())
    val companyDiscoveryState get() = _companyDiscoveryState

    private val _companyDiscoveryAction = MutableSharedFlow<CompanyDiscoveryAction>()
    val companyDiscoveryAction get() = _companyDiscoveryAction

    private val _selectedCompany = MutableStateFlow(SingleCompanyView())
    val selectedCompany get() = _selectedCompany

    private val _companyDetailAction = MutableSharedFlow<CompanyDetailAction>()
    val companyDetailAction get() = _companyDetailAction

    private val _companyFinancials = MutableStateFlow(CompanyFinancialsView())
    val companyFinancials get() = _companyFinancials

    private val _urlToLoad = MutableStateFlow(String())
    val urlToLoad get() = _urlToLoad

    private var companyTicker = ""

    private val selectedConversationId = MutableStateFlow(-1L)

    private val selectedCompanyTicker: String?
        get() = savedStateHandle.get<String>("ticker")

    init {
        getAllSector()
        getAllCompanies()
    }

    fun updateTicker(ticker: String) {
        savedStateHandle["ticker"] = ticker
        getCompany()
    }

    private fun getCompany() {
        selectedCompanyTicker?.let { ticker ->
            _selectedCompany.update { it.copy(loading = true) }
            getCompanyUseCase(ticker) {
                it.onFailure {
                    _selectedCompany.update { state ->
                        state.copy(
                            loading = false,
                            error = "Something went wrong."
                        )
                    }
                }
                it.onSuccess { company ->
                    _selectedCompany.update { view ->
                        view.copy(
                            conversation = CompanyDetailDefaultConversation(
                                id = 0,
                                response = company
                            ),
                            loading = false,
                            companyName = company.name,
                            header = CompanyHeaderPresentation(
                                companyTicker = company.ticker,
                                companyLogo = company.imageUrl,
                                price = company.price,
                                percentageChange = company.change,
                                companyName = company.name
                            )
                        )
                    }
                }
            }
        }
    }

    fun setUrlToLoad(url: String) {
        _urlToLoad.update { url }
    }

    fun getQuery(query: String) {
        _selectedCompany.update { it.copy(inputQuery = query) }
    }

    fun getInputResponse() {
        if (_selectedCompany.value.inputQuery.trim().isNotEmpty()) {
            _selectedCompany.update { it.copy(loading = true) }
            when (_selectedCompany.value.conversation) {
                is CompanyDetailDefaultConversation -> {
                    val prompt = CompanyPrompt(
                        query = _selectedCompany.value.inputQuery,
                        company = (_selectedCompany.value.conversation as CompanyDetailDefaultConversation).response,
                        conversationId = -1L
                    )
                    companyDetailInputResponseUseCase(params = prompt) {
                        it.fold(
                            ::handleCompanyInputResponseFailure,
                            ::handleCompanyInputResponseSuccess
                        )
                    }
                }

                is StructuredConversation -> {
                    val prompt = CompanyPrompt(
                        query = _selectedCompany.value.inputQuery,
                        conversationId = selectedConversationId.value
                    )
                    companyDetailInputResponseUseCase(params = prompt) {
                        it.fold(
                            ::handleCompanyInputResponseFailure,
                            ::handleCompanyInputResponseSuccess
                        )
                    }
                }

                else -> {
                }
            }
        }
    }

    fun getSuggestedPromptResponse(query: String) {
        _selectedCompany.update { it.copy(loading = true) }
        val prompt = CompanyPrompt(
            query = query,
            conversationId = selectedConversationId.value
        )
        companyDetailInputResponseUseCase(params = prompt) {
            it.fold(
                ::handleCompanyInputResponseFailure,
                ::handleCompanyInputResponseSuccess
            )
        }
    }

    private fun handleCompanyInputResponseFailure(failure: Failure) {
        Timber.e(failure.toString())
        _selectedCompany.update {
            it.copy(
                error = "Something went wrong.",
                loading = false,
                inputQuery = ""
            )
        }
    }

    private fun handleCompanyInputResponseSuccess(conversation: Conversation) {
        val s = conversation as StructuredConversation
        Timber.e(s.toString())
        _selectedCompany.update {
            it.copy(
                conversation = conversation,
                loading = false,
                inputQuery = ""
            )
        }
        _selectedCompany.update { it.copy(genText = s.messageList.last().response.toString()) }
        selectedConversationId.update { s.id }
    }

    private fun getAllSector() {
        getAllSectorUseCase(GetAllSectorUseCase.None()) {
            it.fold(
                ::handleGetAllSectorFailure,
                ::handleGetAllSectorSuccess
            )
        }
    }

    private fun handleGetAllSectorFailure(failure: Failure) {
        Timber.e(failure.toString())
    }

    private fun handleGetAllSectorSuccess(sectors: List<SectorInput>) {
        _companyDiscoveryState.update {
            it.copy(sectorView = AllSectorView(sectors = sectors))
        }
    }

    fun selectSector(selected: SectorInput) {
        _companyDiscoveryState.update {
            it.copy(sectorView = it.sectorView.copy(selected = selected))
        }
        getSectorCompanies(selected)
    }

    fun getAllCompanies() {
        _companyDiscoveryState.update { it.copy(companyView = it.companyView.copy(loading = true)) }
        getAllCompaniesUseCase(GetAllCompaniesUseCase.None()) {
            it.fold(
                ::handleAllCompaniesFailure,
                ::handleAllCompaniesSuccess
            )
        }
    }

    private fun handleAllCompaniesFailure(failure: Failure) {
        _companyDiscoveryState.update {
            it.copy(
                companyView = it.companyView.copy(
                    loading = false,
                    error = "Something went wrong."
                )
            )
        }
        Timber.e(failure.toString())
    }

    private fun handleAllCompaniesSuccess(response: List<Company>) {
        getSectorCompanies(_companyDiscoveryState.value.sectorView.selected)

        _companyDiscoveryState.update {
            it.copy(
                companyView = it.companyView.copy(
                    loading = false,
                    companies = response.map { company -> company.toPresentation() }
                )
            )
        }
    }

    private fun getSectorCompanies(sectorInput: SectorInput) {
        val sectorKey = when (sectorInput) {
            is SectorInput.AllSector -> {
                null
            }

            is SectorInput.CustomSector -> {
                sectorInput.sectorKey
            }
        }

        if (_companyDiscoveryState.value.companyView.query.isNotBlank()) {
            performSearch(_companyDiscoveryState.value.companyView.query)
        } else {
            getSectorCompaniesUseCase(sectorKey) {
                it.onSuccess { result ->
                    _companyDiscoveryState.update { state ->
                        state.copy(
                            companyView = state.companyView.copy(
                                loading = false,
                                companies = result.map { company -> company.toPresentation() }
                            )
                        )
                    }
                }
                it.onFailure { failure ->
                    Timber.e(failure.toString())
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _companyDiscoveryState.update {
            it.copy(
                companyView = it.companyView.copy(
                    query = query
                )
            )
        }
        searchCompany()
    }

    fun searchCompany() {
        val query = _companyDiscoveryState.value.companyView.query
        if (query.trim().isNotBlank()) {
            // perform search
            performSearch(query.trim())
        } else {
            // get local companies
            getSectorCompanies(_companyDiscoveryState.value.sectorView.selected)
        }
    }

    private fun performSearch(query: String) {
        val sectorKey = when (_companyDiscoveryState.value.sectorView.selected) {
            is SectorInput.AllSector -> {
                null
            }

            is SectorInput.CustomSector -> {
                val sector =
                    _companyDiscoveryState.value.sectorView.selected as SectorInput.CustomSector
                sector.sectorKey
            }
        }
        val companyQuery = SearchCompanyQuery(query = query, sector = sectorKey)
        searchCompaniesUseCase(companyQuery) {
            it.onSuccess { result ->
                if (result.isNotEmpty()) {
                    _companyDiscoveryState.update { state ->
                        state.copy(
                            companyView = state.companyView.copy(
                                loading = false,
                                companies = result.map { company -> company.toPresentation() }
                            )
                        )
                    }
                } else {
                    // No result found
                    _companyDiscoveryState.update { state ->
                        state.copy(
                            companyView = state.companyView.copy(
                                loading = false,
                                companies = emptyList()
                            )
                        )
                    }
                    Timber.e("No result found")
                }
            }
            it.onFailure { failure ->
                Timber.e(failure.toString())
            }
        }
    }

    fun processCompanyDiscoveryAction(action: CompanyDiscoveryAction) {
        viewModelScope.launch {
            _companyDiscoveryAction.emit(action)
        }
    }

    fun handleCompanyDiscoveryEvent(event: CompanyDiscoveryEvent) {
        when (event) {
            CompanyDiscoveryEvent.PerformSearch -> {
                searchCompany()
            }

            CompanyDiscoveryEvent.RetryCompanies -> {
                getAllCompanies()
            }

            is CompanyDiscoveryEvent.SearchQueryChanged -> {
                updateSearchQuery(event.query)
            }

            is CompanyDiscoveryEvent.SelectSector -> {
                selectSector(event.sector)
            }
        }
    }

    fun handleCompanyDetailEvent(event: CompanyDetailEvent) {
        when (event) {
            CompanyDetailEvent.GoBack -> {
                processCompanyDetailAction(CompanyDetailAction.OnGoBack)
            }

            is CompanyDetailEvent.QueryInputChanged -> {
                getQuery(event.query)
            }

            CompanyDetailEvent.SendClick -> {
                getInputResponse()
            }

            is CompanyDetailEvent.UpdateTicker -> {
                updateTicker(event.ticker)
            }

            is CompanyDetailEvent.SuggestedPromptClicked -> {
                getSuggestedPromptResponse(event.query)
            }
        }
    }

    fun processCompanyDetailAction(action: CompanyDetailAction) {
        viewModelScope.launch {
            _companyDetailAction.emit(action)
        }
    }
}

data class CompanyDiscoveryState(
    val sectorView: AllSectorView = AllSectorView(),
    val companyView: AllCompanyView = AllCompanyView()
)

sealed interface CompanyDiscoveryEvent {
    data class SearchQueryChanged(val query: String) : CompanyDiscoveryEvent
    data object PerformSearch : CompanyDiscoveryEvent
    data class SelectSector(val sector: SectorInput) : CompanyDiscoveryEvent
    data object RetryCompanies : CompanyDiscoveryEvent
}

sealed interface CompanyDiscoveryAction {
    data class OnNavigateToCompanyDetail(val ticker: String) : CompanyDiscoveryAction
}

sealed interface CompanyDetailEvent {
    data class UpdateTicker(val ticker: String) : CompanyDetailEvent
    data class QueryInputChanged(val query: String) : CompanyDetailEvent
    data object GoBack : CompanyDetailEvent
    data object SendClick : CompanyDetailEvent
    data class SuggestedPromptClicked(val query: String) : CompanyDetailEvent
}

sealed interface CompanyDetailAction {
    data object OnGoBack : CompanyDetailAction
    data class OnNavigateToWebView(val url: String) : CompanyDetailAction
}
