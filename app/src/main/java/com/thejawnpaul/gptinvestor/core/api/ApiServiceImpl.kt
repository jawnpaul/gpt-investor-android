package com.thejawnpaul.gptinvestor.core.api

import com.thejawnpaul.gptinvestor.BuildConfig
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
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import javax.inject.Inject
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class ApiServiceImpl @Inject constructor(private val client: HttpClient) : ApiService {
    override suspend fun getCompanies(): Response<List<CompanyRemote>> {
        return client.getAsType<List<CompanyRemote>>("v1/companies")
    }


    override suspend fun getCompanyFinancials(
        request: CompanyFinancialsRequest
    ): Response<CompanyFinancialsRemote> {
        return client.postAsType<CompanyFinancialsRemote>("v1/company", request)
    }

    override suspend fun saveComparison(
        request: SaveComparisonRequest
    ): Response<DefaultSaveResponse> {
        return client.postAsType<DefaultSaveResponse>("v1/save-comparison", request)
    }

    override suspend fun saveSentiment(
        request: SaveSentimentRequest
    ): Response<DefaultSaveResponse> {
        return client.postAsType<DefaultSaveResponse>("v1/save-sentiment", request)
    }

    override suspend fun getAnalystRating(
        request: AnalystRatingRequest
    ): Response<AnalystRatingResponse> {
        return client.postAsType<AnalystRatingResponse>("v1/get-analyst-rating", request)
    }

    override suspend fun saveIndustryRating(
        request: IndustryRatingRequest
    ): Response<DefaultSaveResponse> {
        return client.postAsType<DefaultSaveResponse>("v1/save-industry-rating", request)
    }

    override suspend fun createPdf(request: DownloadPdfRequest): Response<DownloadPdfResponse> {
        return client.postAsType<DownloadPdfResponse>("v1/create-pdf", request)
    }

    override suspend fun getTrendingTickers(): Response<List<TrendingRemote>> {
        return client.getAsType<List<TrendingRemote>>("v1/trending-tickers")
    }

    override suspend fun getDefaultPrompts(): Response<List<DefaultPromptRemote>> {
        return client.getAsType<List<DefaultPromptRemote>>("v1/default-prompts")
    }

    override suspend fun getEntity(request: GetEntityRequest): Response<GetEntityResponse> {
        return client.postAsType<GetEntityResponse>("v1/get-entity", request)
    }

    override suspend fun getCompanyInfo(
        request: CompanyDetailRemoteRequest
    ): Response<CompanyDetailRemoteResponse> {
        return client.postAsType<CompanyDetailRemoteResponse>("v1/company-info", request)
    }

    override suspend fun getCompanyPrice(
        request: CompanyPriceRequest
    ): Response<List<CompanyPriceResponse>> {
        return client.postAsType<List<CompanyPriceResponse>>("v1/company-price", request)
    }

    override suspend fun getTopPicks(date: String): Response<List<TopPickRemote>> {
        return client.getAsType<List<TopPickRemote>>(
            "v1.1/top-picks", mapOf("date" to date)
        )
    }

    override suspend fun registerToken(
        request: RegisterTokenRequest
    ): Response<RegisterTokenResponse> {
        return client.postAsType<RegisterTokenResponse>("v1/notifications/register-token", request)
    }

    override suspend fun addUserToWaitlist(
        request: AddToWaitlistRequest
    ): Response<AddToWaitlistResponse> {
        return client.postAsType<AddToWaitlistResponse>("v1/add-to-waitlist", request)
    }
}

private suspend inline fun <reified T> HttpResponse.response(): Response<T> {
    println("Processing response for type: ${T::class.simpleName}")
    println("Response status: ${status.value} - ${status.description}")
    println("Response headers: $headers")
    println("Response body: ${bodyAsText()}")
    val code = status.value
    val isSuccess = status in HttpStatusCode.OK..HttpStatusCode.PartialContent // 200-299
    val json = Json { ignoreUnknownKeys = true }

    var successBody: T? = null
    var errorString: String?

    if (isSuccess) {
        if (T::class != Unit::class) {
            try {
                // Assuming the body is JSON. Adjust if it can be other types.
                val responseText = bodyAsText()
                if (responseText.isNotBlank()) {
                    successBody = json.decodeFromString<T>(responseText)
                }
            } catch (e: Exception) {
                errorString = "Error parsing response body: ${e.message}"
                return Response.error(code, errorString.toResponseBody())
            }
        } else {
            // Success with no expected body [Unit]
            successBody = Unit as T
            return Response.success(code, successBody)
        }
        return Response.success(code, successBody)
    } else {
        errorString = try {
            bodyAsText()
        } catch (e: Exception) {
            "Error reading error body: ${e.message}"
        }
        return Response.error(code, errorString.toResponseBody())
    }
}

private suspend inline fun <reified T> HttpClient.getAsType(
    urlString: String,
    query: Map<String, String?> = emptyMap(),
): Response<T> {
    println("GET request to: ${BuildConfig.BASE_URL}${urlString}")
    return try {
        val response = this.get {
            url(urlString)
            query.forEach { (key, value) ->
                if (value != null) {
                    url.parameters.append(key, value)
                }
            }
        }
        response.response<T>().also {
            println("Response: ${response.status.value} - ${response.bodyAsText()}")
        }
    } catch (e: RedirectResponseException) {
        println(
            "RedirectResponseException: ${e.response.status.value} - ${e.response.bodyAsText()}"
        )
        return Response.error(
            e.response.status.value,
            e.response.status.description.toResponseBody()
        )
    } catch (e: ClientRequestException) {
        println(
            "ClientRequestException: ${e.response.status.value} - ${e.response.bodyAsText()}"
        )
        return Response.error(
            e.response.status.value,
            e.response.status.description.toResponseBody()
        )
    } catch (e: ServerResponseException) {
        println(
            "ServerResponseException: ${e.response.status.value} - ${e.response.bodyAsText()}"
        )
        return Response.error(
            e.response.status.value,
            e.response.status.description.toResponseBody()
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return Response.error(599, "Unknown error: ${e.message}".toResponseBody())
    }
}

private suspend inline fun <reified T> HttpClient.postAsType(
    url: String,
    body: Any? = null,
): Response<T> {
    println("POST request to: ${BuildConfig.BASE_URL}${url}")
    return try {
        val response = this.post {
            url(url)
            setBody(body)
        }
        response.response<T>().also {
            println("Response: ${response.status.value} - ${response.bodyAsText()}")
        }
    } catch (e: RedirectResponseException) {
        println("RedirectResponseException: ${e.message}")
        return Response.error(
            e.response.status.value,
            e.response.status.description.toResponseBody()
        )
    } catch (e: ClientRequestException) {
        println("ClientRequestException: ${e.message}")
        return Response.error(
            e.response.status.value,
            e.response.status.description.toResponseBody()
        )
    } catch (e: ServerResponseException) {
        println("ServerResponseException: ${e.message}")
        return Response.error(
            e.response.status.value,
            e.response.status.description.toResponseBody()
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return Response.error(599, "Unknown error: ${e.message}".toResponseBody())
    }
}
