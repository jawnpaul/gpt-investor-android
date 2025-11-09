package com.thejawnpaul.gptinvestor.features.investor.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FirebaseAIException
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.generationConfig
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.AnalystRatingRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.DownloadPdfRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.IndustryRatingRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SaveComparisonRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SaveSentimentRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.CompareCompaniesRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.FinalAnalysisRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.GetPdfRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SentimentAnalysisRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SimilarCompanies
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import timber.log.Timber

class InvestorRepository (
    private val apiService: ApiService,
    private val companyDao: CompanyDao
) : IInvestorRepository {
    val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
        modelName = "gemini-1.0-pro",
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
        generationConfig = generationConfig {
            temperature = 0.2f
            topK = 1
            topP = 1f
            maxOutputTokens = 2048
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    override suspend fun getSimilarCompanies(request: SimilarCompanyRequest): Flow<Either<Failure, SimilarCompanies>> = flow {
        try {
            val news = ""
            val systemPrompt =
                "You are a financial analyst assistant. Analyze the given data for ${request.ticker} " +
                    "and suggest a few comparable companies to consider. Do so in a kotlin-parseable list."
            val additional =
                "Historical price data:\n${request.historicalData}\n\nBalance sheet:\n${request.balanceSheet}\n\nFinancial " +
                    "statements:\n${request.financials}\n\nNews articles:\n${news}\n\n---- \n\n Now, " +
                    "suggest a few comparable companies to consider, in a kotlin-parseable list. Return nothing but the list. " +
                    "Make sure the companies are in the form of their tickers."

            val prompt = systemPrompt + additional
            val response = model.generateContent(prompt = prompt)
            val list = getStockSymbols(response.text)
            val obj = SimilarCompanies(codeText = response.text, companies = list)
            emit(Either.Right(obj))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
        }
    }

    override suspend fun compareCompany(request: CompareCompaniesRequest): Flow<Either<Failure, String>> = flow {
        try {
            val otherCompany = apiService.getCompanyFinancials(
                CompanyFinancialsRequest(
                    ticker = request.otherCompanyTicker,
                    years = 1
                )
            )

            val domainObject = otherCompany.toDomainObject()
            // make call to gemini
            val systemPrompt =
                "You are a financial analyst assistant. Compare the data of ${request.currentCompanyTicker} " +
                    "against ${request.currentCompanyTicker} " +
                    "and provide a detailed comparison, like a world-class analyst would. Be measured and discerning. " +
                    "Truly think about the positives and negatives of each company. Be sure of your analysis. " +
                    "You are a skeptical investor."

            val additional =
                "Data for ${request.currentCompanyTicker}:\n\nHistorical price data:\n" +
                    "${request.currentCompany.historicalData}\n\n" +
                    "Balance Sheet:\n${request.currentCompany.balanceSheet}\n\nFinancial Statements:" +
                    "\n${request.currentCompany.financials}\n\n----\n\n" +
                    "Data for ${request.otherCompanyTicker}:\n\nHistorical price data:\n${domainObject.historicalData}\n\n" +
                    "Balance Sheet:\n${domainObject.balanceSheet}\n\nFinancial Statements:\n${domainObject.financials}" +
                    "\n\n----\n\nNow, provide a detailed comparison of ${request.currentCompanyTicker} " +
                    "against ${request.otherCompanyTicker}. " +
                    "Explain your thinking very clearly."
            val prompt = systemPrompt + additional
            val response = model.generateContent(prompt = prompt)
            response.text?.let { geminiText ->
                emit(Either.Right(geminiText))

                val saveRequest = SaveComparisonRequest(
                    mainTicker = request.currentCompanyTicker,
                    otherTicker = request.otherCompanyTicker,
                    geminiText = geminiText
                )
                apiService.saveComparison(saveRequest)
            }
        } catch (e: Exception) {
            when (e) {
                is FirebaseAIException -> {
                    // call api for saved
                }

                else -> {
                    Timber.e(e.stackTraceToString())
                }
            }
        }
    }

    override suspend fun getSentimentAnalysis(request: SentimentAnalysisRequest): Flow<Either<Failure, String>> = flow {
        try {
            var newsString = ""
            request.news.forEach {
                val text = getArticleText(it.link)?.trim()
                newsString += "\n\n---\n\nDate: ${it.relativeDate}\nTitle: ${it.title}\nText: $text"
            }
            val systemPrompt =
                "You are a sentiment analysis assistant. Analyze the sentiment of the given news articles for ${request.ticker} " +
                    "and provide a summary of the overall sentiment and any notable changes " +
                    "over time. Be measured and discerning. You are a skeptical investor."

            val additionalPrompt =
                "News articles for ${request.ticker}:\n${newsString}\n\n----\n\nProvide a summary of the overall sentiment " +
                    "and any notable changes over time."

            val prompt = systemPrompt + additionalPrompt
            val response = model.generateContent(prompt = prompt)
            response.text?.let {
                emit(Either.Right(it))

                val saveRequest = SaveSentimentRequest(ticker = request.ticker, sentiment = it)
                apiService.saveSentiment(saveRequest)
            }
        } catch (e: Exception) {
            when (e) {
                is FirebaseAIException -> {
                    // call api for saved
                }

                else -> {
                    Timber.e(e.stackTraceToString())
                }
            }
        }
    }

    override suspend fun getAnalystRating(ticker: String): Flow<Either<Failure, String>> = flow {
        try {
            val request = AnalystRatingRequest(ticker = ticker)
            val response = apiService.getAnalystRating(request)
            emit(Either.Right(response.rating))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getIndustryAnalysis(ticker: String): Flow<Either<Failure, String>> = flow {
        try {
            val company = companyDao.getCompany(ticker)
            val systemPrompt =
                "You are an industry analysis assistant. Provide an analysis of the ${company.industry} industry and " +
                    "${company.sector} sector, including trends, growth prospects, regulatory changes, and competitive landscape. " +
                    "Be measured and discerning. Truly think about the positives and negatives of the stock. Be sure of your analysis. " +
                    "You are a skeptical investor."
            val additionalPrompt =
                "Provide an analysis of the ${company.industry} industry and ${company.sector} sector."
            val prompt = systemPrompt + additionalPrompt
            val response = model.generateContent(prompt = prompt)
            response.text?.let {
                emit(Either.Right(it))

                val request = IndustryRatingRequest(
                    industry = company.industry,
                    sector = company.sector,
                    rating = it
                )
                apiService.saveIndustryRating(request)
            }
        } catch (e: Exception) {
            when (e) {
                is FirebaseAIException -> {
                    // call api for saved
                }

                else -> {
                    Timber.e(e.stackTraceToString())
                }
            }
        }
    }

    override suspend fun getFinalAnalysis(request: FinalAnalysisRequest): Flow<Either<Failure, String>> = flow {
        try {
            val systemPrompt =
                "You are a financial analyst providing a final investment recommendation for ${request.ticker} based on the given " +
                    "data and analyses. Be measured and discerning. Truly think about the positives and " +
                    "negatives of the stock. Be sure of your analysis. You are a skeptical investor."

            val additionalPrompt =
                "Ticker: ${request.ticker}\n\nComparative Analysis:\n${request.comparison}\n\n" +
                    "Sentiment Analysis:\n${request.sentiment}\n\nAnalyst Ratings:\n${request.analystRating}\n\n" +
                    "Industry Analysis:\n${request.industryRating}\n\nBased on the provided data and analyses, " +
                    "please provide a comprehensive investment analysis and recommendation for ${request.ticker}. " +
                    "Consider the company's financial strength, growth prospects, competitive position, and potential risks. " +
                    "Provide a clear and concise recommendation on whether to buy, hold, or sell the stock, along with " +
                    "supporting rationale."

            val prompt = systemPrompt + additionalPrompt

            val response = model.generateContent(prompt = prompt)

            response.text?.let {
                emit(Either.Right(it))
            }
        } catch (e: Exception) {
            when (e) {
                is FirebaseAIException -> {
                }

                else -> {
                    Timber.e(e.stackTraceToString())
                }
            }
        }
    }

    override suspend fun downloadAnalysisPdf(request: GetPdfRequest): Flow<Either<Failure, String>> = flow {
        try {
            val company = companyDao.getCompany(request.ticker)
            val apiRequest = DownloadPdfRequest(
                name = company.name,
                about = company.summary,
                similarCompanies = request.similarCompanies,
                comparison = request.comparison,
                sentiment = request.sentiment,
                analystRating = request.analystRating,
                industryRating = request.industryRating,
                finalRating = request.finalRating,
                open = request.open,
                high = request.high,
                low = request.low,
                close = request.close,
                volume = request.volume,
                marketCap = request.marketCap
            )
            val response = apiService.createPdf(apiRequest)
            emit(Either.Right(response.url))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    private fun getArticleText(link: String): String? = try {
        var result = ""
        val document = Jsoup.connect(link).get()
        val paragraphs = document.select("p")
        paragraphs.forEach { paragraph ->
            val text = paragraph.text().trim()
            result = "$result $text"
        }
        result
    } catch (e: Exception) {
        Timber.e(e.stackTraceToString())
        null
    }

    private fun getStockSymbols(code: String?): List<String> {
        if (code == null) return emptyList()
        val start = code.indexOf('(') + 1
        val end = code.indexOf(')')
        val sub = code.substring(start, end)
        return sub.splitToSequence(",").map { it.trim() }.toList()
    }
}
