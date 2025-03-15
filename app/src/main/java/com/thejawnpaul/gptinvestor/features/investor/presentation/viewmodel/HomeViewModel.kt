package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetTrendingCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetLocalTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingCompaniesUseCase: GetTrendingCompaniesUseCase,
    private val getTopPicksUseCase: GetTopPicksUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val getLocalTopPicksUseCase: GetLocalTopPicksUseCase,
    private val remoteConfig: RemoteConfig
) :
    ViewModel() {

    private val _trendingCompanies = MutableStateFlow(TrendingCompaniesView())
    val trendingCompanies get() = _trendingCompanies

    private val _topPicks = MutableStateFlow(TopPicksView())
    val topPicks get() = _topPicks

    private val _allTopPicks = MutableStateFlow(TopPicksView())
    val allTopPicks get() = _allTopPicks

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser get() = _currentUser

    init {
        getTrendingCompanies()
        remoteConfig.init()
        getTopPicks()
        getCurrentUser()
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

    fun getTopPicks() {
        _topPicks.update { it.copy(loading = true) }

        getTopPicksUseCase(GetTopPicksUseCase.None()) {
            it.onFailure {
                _topPicks.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
                getLocalPicks()
            }

            it.onSuccess { result ->
                _topPicks.update { state ->
                    state.copy(
                        loading = false,
                        topPicks = result.map { topPick ->
                            with(topPick) {
                                TopPickPresentation(
                                    id,
                                    companyName,
                                    ticker,
                                    rationale,
                                    metrics,
                                    risks,
                                    confidenceScore,
                                    isSaved
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            authenticationRepository.getAuthState().collect { isSignedIn ->
                _currentUser.update { if (isSignedIn) authenticationRepository.currentUser else null }
            }
        }
    }

    private fun getLocalPicks() {
        getLocalTopPicksUseCase(GetLocalTopPicksUseCase.None()) {
            it.onFailure {
                _topPicks.update { state ->
                    state.copy(
                        loading = false,
                        error = "Something went wrong."
                    )
                }
            }

            it.onSuccess { result ->
                _topPicks.update { state ->
                    state.copy(
                        loading = false,
                        topPicks = result.map { topPick ->
                            with(topPick) {
                                TopPickPresentation(
                                    id,
                                    companyName,
                                    ticker,
                                    rationale,
                                    metrics,
                                    risks,
                                    confidenceScore,
                                    isSaved
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
