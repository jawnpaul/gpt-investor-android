package com.example.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.core.functional.onFailure
import com.example.gptinvestor.core.functional.onSuccess
import com.example.gptinvestor.features.company.domain.usecases.GetCompanyFinancialsUseCase
import com.example.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.example.gptinvestor.features.company.presentation.state.CompanyFinancialsView
import com.example.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.example.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.example.gptinvestor.features.investor.domain.model.CompareCompaniesRequest
import com.example.gptinvestor.features.investor.domain.model.SentimentAnalysisRequest
import com.example.gptinvestor.features.investor.domain.usecases.CompareCompaniesUseCase
import com.example.gptinvestor.features.investor.domain.usecases.GetCompanySentimentUseCase
import com.example.gptinvestor.features.investor.domain.usecases.GetSimilarCompaniesUseCase
import com.example.gptinvestor.features.investor.presentation.state.CompanyComparisonView
import com.example.gptinvestor.features.investor.presentation.state.CompanySentimentView
import com.example.gptinvestor.features.investor.presentation.state.SimilarCompaniesView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getCompanyFinancialsUseCase: GetCompanyFinancialsUseCase,
    private val getSimilarCompaniesUseCase: GetSimilarCompaniesUseCase,
    private val compareCompaniesUseCase: CompareCompaniesUseCase,
    private val sentimentUseCase: GetCompanySentimentUseCase
) : ViewModel() {
    private val _selectedCompany = MutableStateFlow(SingleCompanyView())
    val selectedCompany get() = _selectedCompany

    private val _companyFinancials = MutableStateFlow(CompanyFinancialsView())
    val companyFinancials get() = _companyFinancials

    private val _similarCompanies = MutableStateFlow(SimilarCompaniesView())
    val similarCompanies get() = _similarCompanies

    private val _companyComparison = MutableStateFlow(CompanyComparisonView())
    val companyComparison get() = _companyComparison

    private val _companySentiment = MutableStateFlow(CompanySentimentView())
    val companySentiment get() = _companySentiment

    private var companyTicker = ""

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

    fun compareCompanies(ticker: String) {
        _companyFinancials.value.financialsPresentation?.let {
            val request = CompareCompaniesRequest(
                currentCompany = it,
                otherCompanyTicker = ticker,
                currentCompanyTicker = companyTicker
            )
            _companyComparison.update { view ->
                view.copy(loading = true, selectedCompany = ticker)
            }

            compareCompaniesUseCase(request) { res ->
                res.fold(
                    ::handleComparisonFailure,
                    ::handleComparisonSuccess
                )
            }
        }
    }

    private fun handleComparisonFailure(failure: Failure) {
        // make call to get comparison from database
        Timber.e(failure.toString())
        _companyComparison.update { view ->
            view.copy(loading = false, error = "Something went wrong.")
        }
    }

    private fun handleComparisonSuccess(result: String) {
        _companyComparison.update { view ->
            view.copy(loading = false, result = result)
        }
    }

    fun getSentiment() {
        _companyFinancials.value.financialsPresentation?.let {
            val request = SentimentAnalysisRequest(ticker = companyTicker, news = it.news)
            _companySentiment.update { view ->
                view.copy(loading = true)
            }
            sentimentUseCase(request) { res ->
                res.fold(
                    ::handleSentimentFailure,
                    ::handleSentimentSuccess
                )
            }
        }
    }

    private fun handleSentimentFailure(failure: Failure) {
        _companySentiment.update {
            it.copy(loading = false, error = "Something went wrong.")
        }
        Timber.e(failure.toString())
    }

    private fun handleSentimentSuccess(sentiment: String) {
        _companySentiment.update {
            it.copy(loading = false, result = sentiment)
        }
    }
}
