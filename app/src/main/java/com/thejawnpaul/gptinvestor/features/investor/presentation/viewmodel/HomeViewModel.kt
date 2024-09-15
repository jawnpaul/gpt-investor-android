package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetAllCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetAllSectorUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetSectorCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetTrendingCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.state.AllCompanyView
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.AllSectorView
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllSectorUseCase: GetAllSectorUseCase,
    private val getAllCompaniesUseCase: GetAllCompaniesUseCase,
    private val getSectorCompaniesUseCase: GetSectorCompaniesUseCase,
    private val getTrendingCompaniesUseCase: GetTrendingCompaniesUseCase
) :
    ViewModel() {

    private val _allSector =
        MutableStateFlow(AllSectorView())
    val allSector get() = _allSector

    private val _allCompanies =
        MutableStateFlow(AllCompanyView())
    val allCompanies get() = _allCompanies

    private val _trendingCompanies = MutableStateFlow(TrendingCompaniesView())
    val trendingCompanies get() = _trendingCompanies

    init {
        getAllSector()
        getAllCompanies()
        getTrendingCompanies()
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

    fun getAllCompanies() {
        _allCompanies.update {
            it.copy(loading = true, error = null)
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
        Timber.e(failure.toString())
    }

    private fun handleAllCompaniesSuccess(response: List<Company>) {
        getSectorCompanies(_allSector.value.selected)
        /*_allCompanies.update { view ->
            view.copy(loading = false, companies = response.map { it.toPresentation() })
        }*/
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

    fun getTrendingCompanies() {
        _trendingCompanies.update { it.copy(loading = true) }

        getTrendingCompaniesUseCase(GetTrendingCompaniesUseCase.None()) {
            it.onFailure {
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
}
