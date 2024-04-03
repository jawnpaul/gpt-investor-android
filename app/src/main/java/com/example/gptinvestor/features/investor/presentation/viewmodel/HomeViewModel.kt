package com.example.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.core.functional.onFailure
import com.example.gptinvestor.core.functional.onSuccess
import com.example.gptinvestor.features.company.domain.model.Company
import com.example.gptinvestor.features.company.domain.model.SectorInput
import com.example.gptinvestor.features.company.domain.usecases.GetAllCompaniesUseCase
import com.example.gptinvestor.features.company.domain.usecases.GetAllSectorUseCase
import com.example.gptinvestor.features.company.domain.usecases.GetCompanyFinancialsUseCase
import com.example.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.example.gptinvestor.features.company.domain.usecases.GetSectorCompaniesUseCase
import com.example.gptinvestor.features.company.presentation.state.AllCompanyView
import com.example.gptinvestor.features.company.presentation.state.CompanyFinancialsView
import com.example.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.example.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.example.gptinvestor.features.investor.domain.usecases.GetSimilarCompaniesUseCase
import com.example.gptinvestor.features.investor.presentation.state.AllSectorView
import com.example.gptinvestor.features.investor.presentation.state.SimilarCompaniesView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllSectorUseCase: GetAllSectorUseCase,
    private val getAllCompaniesUseCase: GetAllCompaniesUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getCompanyFinancialsUseCase: GetCompanyFinancialsUseCase,
    private val getSimilarCompaniesUseCase: GetSimilarCompaniesUseCase,
    private val getSectorCompaniesUseCase: GetSectorCompaniesUseCase
) :
    ViewModel() {

    private val _allSector =
        MutableStateFlow(AllSectorView())
    val allSector get() = _allSector

    private val _allCompanies =
        MutableStateFlow(AllCompanyView())
    val allCompanies get() = _allCompanies

    private val _selectedCompany = MutableStateFlow(SingleCompanyView())
    val selectedCompany get() = _selectedCompany

    private val _companyFinancials = MutableStateFlow(CompanyFinancialsView())
    val companyFinancials get() = _companyFinancials

    private val _similarCompanies = MutableStateFlow(SimilarCompaniesView())
    val similarCompanies get() = _similarCompanies

    private var companyTicker = ""

    init {
        getAllSector()
        getAllCompanies()
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
        _allSector.update {
            it.copy(sectors = sectors)
        }
    }

    fun selectSector(selected: SectorInput) {
        _allSector.update {
            it.copy(selected = selected)
        }
        getSectorCompanies(selected)
    }

    private fun getAllCompanies() {
        _allCompanies.update {
            it.copy(loading = true)
        }
        getAllCompaniesUseCase(GetAllCompaniesUseCase.None()) {
            it.fold(
                ::handleAllCompaniesFailure,
                ::handleAllCompaniesSuccess
            )
        }
    }

    private fun handleAllCompaniesFailure(failure: Failure) {
        _allCompanies.update {
            it.copy(loading = false, error = "Something went wrong.")
        }
        // TODO:Handle error to show retry button i.e if current list is empty and error happened
        Timber.e(failure.toString())
    }

    private fun handleAllCompaniesSuccess(response: List<Company>) {
        getSectorCompanies(_allSector.value.selected)
        /*_allCompanies.update { view ->
            view.copy(loading = false, companies = response.map { it.toPresentation() })
        }*/
    }

    fun getCompany(ticker: String) {
        companyTicker = ticker
        getCompanyFinancials(ticker)
        getCompanyUseCase(ticker) {
            it.onFailure {
            }

            it.onSuccess { company ->
                _selectedCompany.update { view ->
                    view.copy(company = company.toPresentation())
                }
            }
        }
    }

    private fun getCompanyFinancials(ticker: String) {
        getCompanyFinancialsUseCase(ticker) {
            it.onSuccess {
            }

            it.onSuccess { result ->
                _companyFinancials.update { view ->
                    view.copy(financialsPresentation = result.toPresentation())
                }
            }
        }
    }

    fun getSimilarCompanies() {
        _companyFinancials.value.financialsPresentation?.let {
            val request = SimilarCompanyRequest(
                ticker = companyTicker,
                historicalData = it.historicalData,
                balanceSheet = it.balanceSheet,
                financials = it.financials,
                news = it.news
            )
            _similarCompanies.update { view ->
                view.copy(loading = true)
            }
            getSimilarCompaniesUseCase(request) { aa ->
                aa.onSuccess { res ->
                    _similarCompanies.update { view ->
                        view.copy(loading = false, result = res)
                    }
                }
                aa.onFailure {
                }
                    _similarCompanies.update { view ->
                        view.copy(loading = false, error = "Something went wrong.")
                    }
                }
            }
        }
    }

    fun resetSimilarCompanies() {
        _similarCompanies.update {
            SimilarCompaniesView()
        }
    }

            }
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

        getSectorCompaniesUseCase(sectorKey) {
            it.onSuccess { result ->
                _allCompanies.update { view ->
                    view.copy(loading = false, companies = result.map { aa -> aa.toPresentation() })
                }
            }
            it.onFailure { failure ->
                Timber.e(failure.toString())
            }
        }
    }
}
