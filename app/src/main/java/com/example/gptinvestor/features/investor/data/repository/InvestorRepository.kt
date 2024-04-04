package com.example.gptinvestor.features.investor.data.repository

import com.example.gptinvestor.BuildConfig
import com.example.gptinvestor.core.api.ApiService
import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.example.gptinvestor.features.investor.data.remote.SaveComparisonRequest
import com.example.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.example.gptinvestor.features.investor.domain.model.CompareCompaniesRequest
import com.example.gptinvestor.features.investor.domain.model.SentimentAnalysisRequest
import com.example.gptinvestor.features.investor.domain.model.SimilarCompanies
import com.example.gptinvestor.features.investor.domain.repository.IInvestorRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import timber.log.Timber

class InvestorRepository @Inject constructor(private val apiService: ApiService) :
    IInvestorRepository {
    val model = GenerativeModel(
        modelName = "gemini-1.0-pro",
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.2f
            topK = 1
            topP = 1f
            maxOutputTokens = 2048
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    override suspend fun getSimilarCompanies(request: SimilarCompanyRequest): Flow<Either<Failure, SimilarCompanies>> = flow {
        try {
            var news = ""
                /*request.news.take(1).forEach {
                    val text = getArticleText(it.link)?.trim()
                    news += "\n\n --- \n\nTitle:${it.title} \nText:$text"
                }*/
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
            if (otherCompany.isSuccessful) {
                otherCompany.body()?.let {
                    val domainObject = it.toDomainObject()
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
                }
            } else {
                emit(Either.Left(Failure.UnAvailableError))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            when (e) {
                is IllegalArgumentException -> {
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
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
        }
    }

    private fun getArticleText(link: String): String? {
        return try {
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
    }

    private fun getStockSymbols(code: String?): List<String> {
        if (code == null) return emptyList()
        val start = code.indexOf('(') + 1
        val end = code.indexOf(')')
        val sub = code.substring(start, end)
        return sub.splitToSequence(",").map { it.trim() }.toList()
    }
}
