package com.thejawnpaul.gptinvestor.features.toppick.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetSingleTopPickUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPickDetailView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

@HiltViewModel
class TopPickViewModel @Inject constructor(
    private val getSingleTopPickUseCase: GetSingleTopPickUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val topPickId: Long?
        get() = savedStateHandle.get<Long>("topPickId")

    private val _topPickView = MutableStateFlow(TopPickDetailView())
    val topPickView get() = _topPickView

    fun updateTopPickId(topPickId: String) {
        savedStateHandle["topPickId"] = topPickId.toLong()
        getTopPick()
    }

    private fun getTopPick() {
        _topPickView.update { it.copy(loading = true) }
        topPickId?.let { id ->
            getSingleTopPickUseCase(id) {
                it.fold(
                    ::handleGetTopPickFailure,
                    ::handleGetTopPickSuccess
                )
            }
        }
    }

    private fun handleGetTopPickFailure(failure: Failure) {
        _topPickView.update { it.copy(loading = false) }
        Timber.e(failure.toString())
    }

    private fun handleGetTopPickSuccess(topPick: TopPick) {
        _topPickView.update {
            it.copy(
                loading = false,
                topPick = with(topPick) {
                    TopPickPresentation(
                        id = id,
                        companyName = companyName,
                        ticker = ticker,
                        rationale = rationale,
                        metrics = metrics,
                        risks = risks,
                        confidenceScore = confidenceScore
                    )
                }
            )
        }
        Timber.e(topPick.toString())
    }

    fun loginUser() {
        _topPickView.update { it.copy(isLoggedIn = true) }
    }
}
