package com.example.gptinvestor.features.investor.data.repository

import com.example.gptinvestor.BuildConfig
import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
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

class InvestorRepository @Inject constructor() : IInvestorRepository {

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

    override suspend fun getSimilarCompanies(request: SimilarCompanyRequest): Flow<Either<Failure, List<String>>> = flow {
        try {
            var news = ""
            request.news.take(1).forEach {
                val text = getArticleText(it.link)?.trim()
                news += "\n\n --- \n\nTitle:${it.title} \nText:$text"
            }
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
            Timber.e(response.text)
            val list = getStockSymbols(response.text)
            emit(Either.Right(list))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
        }
    }

    private suspend fun getArticleText(link: String): String? {
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
        val trimmedCode = code.trim()
        Timber.e(trimmedCode)
        val aa = trimmedCode.removePrefix("```kotlin")
        Timber.e(aa)
        // Remove leading/trailing whitespace and ```
        if (!aa.startsWith("listOf(")) {
            throw IllegalArgumentException("Invalid input format. Expected listOf( symbols)")
        }
        val symbolsString = aa.substringAfter("(") // Get everything after "("
        return symbolsString.substringBeforeLast(")").splitToSequence(",") // Split by comma and remove trailing )
            .map { it.trim() } // Remove leading/trailing spaces from each symbol
            .toList()
    }
}
