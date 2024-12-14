package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetTrendingCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingCompaniesUseCase: GetTrendingCompaniesUseCase
) :
    ViewModel() {

    private val _trendingCompanies = MutableStateFlow(TrendingCompaniesView())
    val trendingCompanies get() = _trendingCompanies

    init {
        getTrendingCompanies()
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
