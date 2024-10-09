package com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.history.domain.usecases.GetAllHistoryUseCase
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryScreenView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val getAllHistoryUseCase: GetAllHistoryUseCase) :
    ViewModel() {

    private val _historyScreenView = MutableStateFlow(HistoryScreenView())
    val historyScreenView get() = _historyScreenView

    init {
        getAllHistory()
    }

    private fun getAllHistory() {
        _historyScreenView.update { it.copy(loading = true) }
        getAllHistoryUseCase(GetAllHistoryUseCase.None()) {
            it.fold(
                ::handleGetAllHistoryFailure,
                ::handleGetAllHistorySuccess
            )
        }
    }

    private fun handleGetAllHistoryFailure(failure: Failure) {
        _historyScreenView.update { it.copy(loading = false) }
        Timber.e(failure.toString())
    }

    private fun handleGetAllHistorySuccess(response: List<StructuredConversation>) {
        _historyScreenView.update { it.copy(loading = false, list = response) }

    }
}