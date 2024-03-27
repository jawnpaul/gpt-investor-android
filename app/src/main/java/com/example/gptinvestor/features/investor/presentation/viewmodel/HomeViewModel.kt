package com.example.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.investor.domain.model.SectorInput
import com.example.gptinvestor.features.investor.domain.usecases.GetAllSectorUseCase
import com.example.gptinvestor.features.investor.presentation.state.AllSectorView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val getAllSectorUseCase: GetAllSectorUseCase) :
    ViewModel() {

    private val _allSector =
        MutableStateFlow(AllSectorView())
    val allSector get() = _allSector

    init {
        getAllSector()
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
        Timber.e(selected.toString())
        _allSector.update {
            it.copy(selected = selected)
        }
    }
}