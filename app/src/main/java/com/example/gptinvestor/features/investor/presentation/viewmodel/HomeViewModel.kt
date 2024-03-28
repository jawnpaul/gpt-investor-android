package com.example.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.company.domain.model.Company
import com.example.gptinvestor.features.company.domain.model.SectorInput
import com.example.gptinvestor.features.company.domain.usecases.GetAllCompaniesUseCase
import com.example.gptinvestor.features.company.domain.usecases.GetAllSectorUseCase
import com.example.gptinvestor.features.company.presentation.state.AllCompanyView
import com.example.gptinvestor.features.investor.presentation.state.AllSectorView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllSectorUseCase: GetAllSectorUseCase,
    private val getAllCompaniesUseCase: GetAllCompaniesUseCase
) :
    ViewModel() {

    private val _allSector =
        MutableStateFlow(AllSectorView())
    val allSector get() = _allSector

    private val _allCompanies =
        MutableStateFlow(AllCompanyView())
    val allCompanies get() = _allCompanies

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
    }

    private fun handleAllCompaniesSuccess(response: List<Company>) {
        _allCompanies.update { view ->
            view.copy(loading = false, companies = response.map { it.toPresentation() })
        }
    }
}
