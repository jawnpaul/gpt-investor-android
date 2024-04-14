package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyFinancialsUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyFinancialsView
import com.thejawnpaul.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.CompareCompaniesRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.FinalAnalysisRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.GetPdfRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SentimentAnalysisRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.usecases.CompareCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.investor.domain.usecases.DownloadPdfUseCase
import com.thejawnpaul.gptinvestor.features.investor.domain.usecases.GetAnalystRatingUseCase
import com.thejawnpaul.gptinvestor.features.investor.domain.usecases.GetCompanySentimentUseCase
import com.thejawnpaul.gptinvestor.features.investor.domain.usecases.GetFinalRatingUseCase
import com.thejawnpaul.gptinvestor.features.investor.domain.usecases.GetIndustryRatingUseCase
import com.thejawnpaul.gptinvestor.features.investor.domain.usecases.GetSimilarCompaniesUseCase
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.AnalystRatingView
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.CompanyComparisonView
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.CompanySentimentView
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.DownloadPdfView
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.FinalAnalysisView
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.IndustryRatingView
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.SimilarCompaniesView
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
    private val sentimentUseCase: GetCompanySentimentUseCase,
    private val analystRatingUseCase: GetAnalystRatingUseCase,
    private val industryRatingUseCase: GetIndustryRatingUseCase,
    private val finalRatingUseCase: GetFinalRatingUseCase,
    private val downloadPdfUseCase: DownloadPdfUseCase

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

    private val _analystRating = MutableStateFlow(AnalystRatingView())
    val analystRating get() = _analystRating

    private val _industryRating = MutableStateFlow(IndustryRatingView())
    val industryRating get() = _industryRating

    private val _finalAnalysis = MutableStateFlow(FinalAnalysisView())
    val finalAnalysis get() = _finalAnalysis

    private val _downloadPdf = MutableStateFlow(DownloadPdfView())
    val downloadPdf get() = _downloadPdf

    private val _urlToLoad = MutableStateFlow(String())
    val urlToLoad get() = _urlToLoad

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab get() = _selectedTab

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
        resetCompanyComparison()
        resetGenerativeAIViews()
    }

    fun compareCompanies(ticker: String) {
        _companyFinancials.value.financialsPresentation?.let {
            val request = CompareCompaniesRequest(
                currentCompany = it,
                otherCompanyTicker = ticker,
                currentCompanyTicker = companyTicker
            )
            _companyComparison.update { view ->
                view.copy(loading = true, selectedCompany = ticker, result = null)
            }

            compareCompaniesUseCase(request) { res ->
                res.fold(
                    ::handleComparisonFailure,
                    ::handleComparisonSuccess
                )
            }
            // reset the gen AI views if a new company is selected
            resetGenerativeAIViews()
        }
    }

    private fun handleComparisonFailure(failure: Failure) {
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

    fun getAnalystRating() {
        _analystRating.update {
            it.copy(loading = true)
        }
        analystRatingUseCase(companyTicker) {
            it.fold(
                ::handleAnalystRatingFailure,
                ::handleAnalystRatingSuccess
            )
        }
    }

    private fun handleAnalystRatingFailure(failure: Failure) {
        _analystRating.update {
            it.copy(loading = false, error = "Something went wrong.")
        }
        Timber.e(failure.toString())
    }

    private fun handleAnalystRatingSuccess(rating: String) {
        _analystRating.update {
            it.copy(loading = false, result = rating)
        }
    }

    fun getIndustryRating() {
        _industryRating.update {
            it.copy(loading = true)
        }
        industryRatingUseCase(companyTicker) {
            it.fold(
                ::handleIndustryRatingFailure,
                ::handleIndustryRatingSuccess
            )
        }
    }

    private fun handleIndustryRatingFailure(failure: Failure) {
        _industryRating.update {
            it.copy(loading = false, error = "Something went wrong.")
        }
        Timber.e(failure.toString())
    }

    private fun handleIndustryRatingSuccess(rating: String) {
        _industryRating.update {
            it.copy(loading = false, result = rating)
        }
    }

    fun getFinalRating() {
        _finalAnalysis.update {
            it.copy(loading = true)
        }
        val request = FinalAnalysisRequest(
            ticker = companyTicker,
            comparison = _companyComparison.value.result,
            sentiment = _companySentiment.value.result,
            analystRating = "",
            industryRating = ""
        )
        finalRatingUseCase(request) {
            it.fold(
                ::handleFinalRatingFailure,
                ::handleFinalRatingSuccess
            )
        }
    }

    private fun handleFinalRatingFailure(failure: Failure) {
        _finalAnalysis.update {
            it.copy(loading = false, error = "Something went wrong.")
        }
        Timber.e(failure.toString())
    }

    private fun handleFinalRatingSuccess(rating: String) {
        _finalAnalysis.update {
            it.copy(loading = false, result = rating)
        }
    }

    private fun resetCompanyComparison() {
        _companyComparison.update {
            CompanyComparisonView()
        }
    }

    private fun resetGenerativeAIViews() {
        _companySentiment.update {
            CompanySentimentView()
        }

        _analystRating.update {
            AnalystRatingView()
        }

        _industryRating.update {
            IndustryRatingView()
        }

        _finalAnalysis.update {
            FinalAnalysisView()
        }

        _downloadPdf.update {
            DownloadPdfView()
        }
    }

    fun downloadPdf() {
        val request = GetPdfRequest(
            ticker = companyTicker,
            finalRating = _finalAnalysis.value.result ?: "",
            similarCompanies = _similarCompanies.value.result?.codeText ?: "",
            sentiment = _companySentiment.value.result ?: "",
            comparison = _companyComparison.value.result ?: "",
            analystRating = _analystRating.value.result ?: "",
            industryRating = _industryRating.value.result ?: "",
            open = _companyFinancials.value.financialsPresentation?.open ?: "",
            high = _companyFinancials.value.financialsPresentation?.high ?: "",
            low = _companyFinancials.value.financialsPresentation?.low ?: "",
            close = _companyFinancials.value.financialsPresentation?.close ?: "",
            volume = _companyFinancials.value.financialsPresentation?.volume ?: "",
            marketCap = _companyFinancials.value.financialsPresentation?.marketCap ?: ""
        )
        _downloadPdf.update {
            it.copy(loading = true)
        }
        downloadPdfUseCase(request) {
            it.fold(
                ::handleDownloadFailure,
                ::handleDownloadSuccess
            )
        }
    }

    private fun handleDownloadFailure(failure: Failure) {
        _downloadPdf.update {
            it.copy(loading = false, error = "Something went wrong.")
        }
        Timber.e(failure.toString())
    }

    private fun handleDownloadSuccess(url: String) {
        _downloadPdf.update {
            it.copy(loading = false, result = url)
        }
    }

    fun setUrlToLoad(url: String) {
        _urlToLoad.update { url }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.update { tabIndex }
    }
}
