package com.thejawnpaul.gptinvestor.features.toppick.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetLocalTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetSavedTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetSingleTopPickUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.RemoveTopPickFromSavedUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.SaveTopPickUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.ShareTopPickUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPickDetailView
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TopPickViewModel @Inject constructor(
    private val getSingleTopPickUseCase: GetSingleTopPickUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getLocalTopPicksUseCase: GetLocalTopPicksUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val saveTopPickUseCase: SaveTopPickUseCase,
    private val removeTopPickFromSavedUseCase: RemoveTopPickFromSavedUseCase,
    private val getSavedTopPicksUseCase: GetSavedTopPicksUseCase,
    private val shareTopPickUseCase: ShareTopPickUseCase,
    private val getCompanyUseCase: GetCompanyUseCase
) : ViewModel() {

    private val topPickId: String?
        get() = savedStateHandle.get<String>("topPickId")

    private val _topPickView = MutableStateFlow(TopPickDetailView())
    val topPickView get() = _topPickView

    private val _actions = MutableSharedFlow<TopPickAction>()
    val actions get() = _actions

    private val _allTopPicks = MutableStateFlow(TopPicksView())
    val allTopPicks get() = _allTopPicks

    private val _savedTopPicks = MutableStateFlow(TopPicksView())
    val savedTopPicks get() = _savedTopPicks

    init {
        viewModelScope.launch {
            authenticationRepository.getAuthState().collect { isSignedIn ->
                _topPickView.update { it.copy(isLoggedIn = isSignedIn) }
            }
        }
    }

    fun updateTopPickId(topPickId: String) {
        savedStateHandle["topPickId"] = topPickId
        getTopPick()
    }

    private fun getTopPick() {
        Timber.e(topPickId.toString())
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
                        confidenceScore = confidenceScore,
                        isSaved = isSaved,
                        imageUrl = imageUrl,
                        percentageChange = percentageChange,
                        currentPrice = currentPrice
                    )
                }
            )
        }
        getCompanyFinancials(ticker = topPick.ticker)
        Timber.e(topPick.toString())
    }

    fun getAllTopPicks() {
        _allTopPicks.update { it.copy(loading = true) }

        getLocalTopPicksUseCase(GetLocalTopPicksUseCase.None()) {
            it.onFailure {
                _allTopPicks.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
            }

            it.onSuccess { result ->
                _allTopPicks.update { state ->
                    state.copy(
                        loading = false,
                        topPicks = result.map { topPick ->
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
                    )
                }
            }
        }
    }

    fun handleSave() {
        if (_topPickView.value.topPick?.isSaved == true) {
            removeTopPickFromSaved()
        } else {
            saveTopPick()
        }
    }

    private fun saveTopPick() {
        topPickId?.let { id ->
            saveTopPickUseCase(id) {
                it.onSuccess { topPick ->
                    _topPickView.update { state ->
                        state.copy(
                            loading = false,
                            topPick = with(topPick) {
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
                        )
                    }
                }

                it.onFailure { failure ->
                    Timber.e(failure.toString())
                }
            }
        }
    }

    private fun removeTopPickFromSaved() {
        topPickId?.let { id ->
            removeTopPickFromSavedUseCase(id) {
                it.onSuccess { topPick ->
                    _topPickView.update { state ->
                        state.copy(
                            loading = false,
                            topPick = with(topPick) {
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
                        )
                    }
                }

                it.onFailure { failure ->
                    Timber.e(failure.toString())
                }
            }
        }
    }

    fun getSavedTopPicks() {
        getSavedTopPicksUseCase(GetSavedTopPicksUseCase.None()) {
            it.onFailure {
                _savedTopPicks.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
            }

            it.onSuccess { result ->
                _savedTopPicks.update { state ->
                    state.copy(
                        loading = false,
                        topPicks = result.map { topPick ->
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
                    )
                }
            }
        }
    }

    fun shareTopPick() {
        topPickId?.let { id ->
            shareTopPickUseCase(id) {
                it.onSuccess { result ->
                    viewModelScope.launch {
                        _actions.emit(TopPickAction.OnShare(result))
                    }
                }
                it.onFailure {
                    Timber.e(it.toString())
                }
            }
        }
    }

    fun getCompanyFinancials(ticker: String) {
        getCompanyUseCase(ticker) {
            it.onSuccess { result ->
                _topPickView.update { state ->
                    state.copy(
                        companyPresentation = result
                    )
                }
            }
            it.onFailure {
                Timber.e(it.toString())
            }
        }
    }

    fun handleEvent(event: TopPickEvent) {
        when (event) {
            is TopPickEvent.Authenticate -> {
                _topPickView.update { it.copy(showAuthenticateDialog = event.showDialog) }
            }
        }
    }
}

sealed interface TopPickAction {
    data class OnShare(val url: String) : TopPickAction
}

sealed interface TopPickEvent {
    data class Authenticate(val showDialog: Boolean) : TopPickEvent
}
