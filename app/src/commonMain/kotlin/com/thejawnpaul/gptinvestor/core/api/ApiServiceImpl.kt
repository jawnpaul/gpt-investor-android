package com.thejawnpaul.gptinvestor.core.api

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRemote
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyPriceRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyPriceResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyRemote
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.TrendingRemote
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.AddToWaitlistRequest
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.AddToWaitlistResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.DefaultPromptRemote
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.GetEntityRequest
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.GetEntityResponse
import com.thejawnpaul.gptinvestor.features.investor.data.remote.AnalystRatingRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.AnalystRatingResponse
import com.thejawnpaul.gptinvestor.features.investor.data.remote.DefaultSaveResponse
import com.thejawnpaul.gptinvestor.features.investor.data.remote.DownloadPdfRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.DownloadPdfResponse
import com.thejawnpaul.gptinvestor.features.investor.data.remote.IndustryRatingRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SaveComparisonRequest
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SaveSentimentRequest
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenRequest
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenResponse
import com.thejawnpaul.gptinvestor.features.toppick.data.remote.TopPickRemote
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class ApiServiceImpl (private val client: HttpClient) : ApiService {
    override suspend fun getCompanies(): Result<List<CompanyRemote>> {
        return client.getAsType<List<CompanyRemote>>("v1/companies")
    }

    override suspend fun getCompanyFinancials(request: CompanyFinancialsRequest): Result<CompanyFinancialsRemote> {
        return client.postAsType<CompanyFinancialsRemote>("v1/company", request)
    }

    override suspend fun saveComparison(request: SaveComparisonRequest): Result<DefaultSaveResponse> {
        return client.postAsType<DefaultSaveResponse>("v1/save-comparison", request)
    }

    override suspend fun saveSentiment(request: SaveSentimentRequest): Result<DefaultSaveResponse> {
        return client.postAsType<DefaultSaveResponse>("v1/save-sentiment", request)
    }

    override suspend fun getAnalystRating(request: AnalystRatingRequest): Result<AnalystRatingResponse> {
        return client.postAsType<AnalystRatingResponse>("v1/get-analyst-rating", request)
    }

    override suspend fun saveIndustryRating(request: IndustryRatingRequest): Result<DefaultSaveResponse> {
        return client.postAsType<DefaultSaveResponse>("v1/save-industry-rating", request)
    }

    override suspend fun createPdf(request: DownloadPdfRequest): Result<DownloadPdfResponse> {
        return client.postAsType<DownloadPdfResponse>("v1/create-pdf", request)
    }

    override suspend fun getTrendingTickers(): Result<List<TrendingRemote>> {
        return client.getAsType<List<TrendingRemote>>("v1/trending-tickers")
    }

    override suspend fun getDefaultPrompts(): Result<List<DefaultPromptRemote>> {
        return client.getAsType<List<DefaultPromptRemote>>("v1/default-prompts")
    }

    override suspend fun getEntity(request: GetEntityRequest): Result<GetEntityResponse> {
        return client.postAsType<GetEntityResponse>("v1/get-entity", request)
    }

    override suspend fun getCompanyInfo(request: CompanyDetailRemoteRequest): Result<CompanyDetailRemoteResponse> {
        return client.postAsType<CompanyDetailRemoteResponse>("v1/company-info", request)
    }

    override suspend fun getCompanyPrice(request: CompanyPriceRequest): Result<List<CompanyPriceResponse>> {
        return client.postAsType<List<CompanyPriceResponse>>("v1/company-price", request)
    }

    override suspend fun getTopPicks(date: String): Result<List<TopPickRemote>> {
        return client.getAsType<List<TopPickRemote>>(
            "v1.1/top-picks",
            mapOf("date" to date)
        )
    }

    override suspend fun registerToken(request: RegisterTokenRequest): Result<RegisterTokenResponse> {
        return client.postAsType<RegisterTokenResponse>(
            "v1/notifications/register-token",
            request
        )
    }

    override suspend fun addUserToWaitlist(request: AddToWaitlistRequest): Result<AddToWaitlistResponse> {
        return client.postAsType<AddToWaitlistResponse>("v1/add-to-waitlist", request)
    }
}

private suspend inline fun <reified T> HttpResponse.Result(): Result<T> {
    val code = status.value
    val isSuccess = status in HttpStatusCode.OK..HttpStatusCode.PartialContent // 200-299
    val json = Json { ignoreUnknownKeys = true }

    var successBody: T? = null
    var errorString: String?

    if (isSuccess) {
        if (T::class != Unit::class) {
            try {
                // Assuming the body is JSON. Adjust if it can be other types.
                val ResultText = bodyAsText()
                if (ResultText.isNotBlank()) {
                    successBody = json.decodeFromString<T>(ResultText)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorString = "Error parsing Result body: ${e.message}"
                return Result.failure(Exception(errorString))
            }
        } else {
            // Success with no expected body [Unit]
            successBody = Unit as T
            return Result.success(successBody)
        }
        return Result.success(successBody) as Result<T>
    } else {
        errorString = try {
            bodyAsText()
        } catch (e: Exception) {
            e.printStackTrace()
            "Error reading error body: ${e.message}"
        }
        println("Error code: $code; Error Result: $errorString")
        return Result.failure(Exception(errorString))
    }
}

private suspend inline fun <reified T> HttpClient.getAsType(urlString: String, query: Map<String, String?> = emptyMap()): Result<T> {
    val Result = this.get {
        url(urlString)
        query.forEach { (key, value) ->
            if (value != null) {
                url.parameters.append(key, value)
            }
        }
    }
    return Result.Result<T>()
}

private suspend inline fun <reified T> HttpClient.postAsType(url: String, body: Any? = null): Result<T> {
    val Result = this.post {
        contentType(ContentType.Application.Json.withParameter("charset", "UTF-8"))
        url(url)
        setBody(body)
    }
    return Result.Result<T>()
}
