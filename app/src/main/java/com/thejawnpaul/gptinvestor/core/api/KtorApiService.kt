package com.thejawnpaul.gptinvestor.core.api

import com.thejawnpaul.gptinvestor.features.authentication.data.remote.FirebaseLoginRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.LoginRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.LoginResponse
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.SignUpRequest
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.SignUpResponse
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
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.AiChatRequest
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.DefaultPromptRemote
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.GetEntityRequest
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.GetEntityResponse
import com.thejawnpaul.gptinvestor.features.billing.data.remote.VerifyPurchaseRequest
import com.thejawnpaul.gptinvestor.features.billing.data.remote.VerifyPurchaseResponse
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenRequest
import com.thejawnpaul.gptinvestor.features.notification.data.RegisterTokenResponse
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.AllTidbitResponse
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitBookmarkRequest
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitBookmarkResponse
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitLikeRequest
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitLikeResponse
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitRemote
import com.thejawnpaul.gptinvestor.features.toppick.data.remote.TopPickRemote
import com.thejawnpaul.gptinvestor.remote.TokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KtorApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getCompanies(): KtorResponse<List<CompanyRemote>> =
        client.get("v1/companies").toKtorResponse()

    suspend fun getCompanyFinancials(request: CompanyFinancialsRequest): KtorResponse<CompanyFinancialsRemote> =
        client.post("v1/company") {
            setBody(request)
        }.toKtorResponse()

    suspend fun getTrendingTickers(): KtorResponse<List<TrendingRemote>> =
        client.get("v1/trending-tickers").toKtorResponse()

    suspend fun getDefaultPrompts(): KtorResponse<List<DefaultPromptRemote>> =
        client.get("v1/default-prompts").toKtorResponse()

    suspend fun getEntity(request: GetEntityRequest): KtorResponse<GetEntityResponse> =
        client.post("v1/get-entity") {
            setBody(request)
        }.toKtorResponse()

    suspend fun getCompanyInfo(request: CompanyDetailRemoteRequest): KtorResponse<CompanyDetailRemoteResponse> =
        client.post("v1/company-info") {
            setBody(request)
        }.toKtorResponse()

    suspend fun getCompanyPrice(request: CompanyPriceRequest): KtorResponse<List<CompanyPriceResponse>> =
        client.post("v1/company-price") {
            setBody(request)
        }.toKtorResponse()

    suspend fun getTopPicks(date: String): KtorResponse<List<TopPickRemote>> =
        client.get("v1.1/top-picks") {
            parameter("date", date)
        }.toKtorResponse()

    suspend fun registerToken(request: RegisterTokenRequest): KtorResponse<RegisterTokenResponse> =
        client.post("v1/notifications/register-token") {
            setBody(request)
        }.toKtorResponse()

    suspend fun addUserToWaitlist(request: AddToWaitlistRequest): KtorResponse<AddToWaitlistResponse> =
        client.post("v1/add-to-waitlist") {
            setBody(request)
        }.toKtorResponse()

    suspend fun getAllTidbit(page: Int = 1, pageSize: Int = 10): KtorResponse<AllTidbitResponse> =
        client.get("v1/tidbit/all-tidbit") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.toKtorResponse()

    suspend fun getLatestTidbits(page: Int = 1, pageSize: Int = 10): KtorResponse<AllTidbitResponse> =
        client.get("v1/tidbit/latest-tidbits") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.toKtorResponse()

    suspend fun getTrendingTidbit(page: Int = 1, pageSize: Int = 10): KtorResponse<AllTidbitResponse> =
        client.get("v1/tidbit/trending-tidbits") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.toKtorResponse()

    suspend fun getSavedTidbits(page: Int = 1, pageSize: Int = 10): KtorResponse<AllTidbitResponse> =
        client.get("v1/tidbit/bookmarked-tidbits") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.toKtorResponse()

    suspend fun getTodayTidbit(): KtorResponse<TidbitRemote> =
        client.get("v1/tidbit/today-tidbit").toKtorResponse()

    suspend fun getSingleTidbit(id: String): KtorResponse<TidbitRemote> =
        client.get("v1/tidbit/single-tidbit") {
            parameter("id", id)
        }.toKtorResponse()

    suspend fun likeTidbit(request: TidbitLikeRequest): KtorResponse<TidbitLikeResponse> =
        client.post("v1/tidbit/like-tidbit") {
            setBody(request)
        }.toKtorResponse()

    suspend fun unlikeTidbit(request: TidbitLikeRequest): KtorResponse<TidbitLikeResponse> =
        client.post("v1/tidbit/unlike-tidbit") {
            setBody(request)
        }.toKtorResponse()

    suspend fun bookmarkTidbit(request: TidbitBookmarkRequest): KtorResponse<TidbitBookmarkResponse> =
        client.post("v1/tidbit/bookmark-tidbit") {
            setBody(request)
        }.toKtorResponse()

    suspend fun removeBookmark(request: TidbitBookmarkRequest): KtorResponse<TidbitBookmarkResponse> =
        client.post("v1/tidbit/unbookmark-tidbit") {
            setBody(request)
        }.toKtorResponse()

    suspend fun chatAiResponse(request: AiChatRequest): HttpResponse =
        client.post("v1/ai/chat") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    suspend fun loginWithEmailAndPassword(request: LoginRequest): KtorResponse<LoginResponse> =
        client.post("v1.1/login") {
            setBody(request)
        }.toKtorResponse()

    suspend fun loginWithFirebase(request: FirebaseLoginRequest): KtorResponse<LoginResponse> =
        client.post("v1.1/firebase-login") {
            setBody(request)
        }.toKtorResponse()

    suspend fun signUpWithEmailAndPassword(request: SignUpRequest): KtorResponse<SignUpResponse> =
        client.post("v1.1/register") {
            setBody(request)
        }.toKtorResponse()

    suspend fun verifyPlayPurchase(request: VerifyPurchaseRequest): KtorResponse<VerifyPurchaseResponse> =
        client.post("v1/google-play/verify") {
            setBody(request)
        }.toKtorResponse()

    suspend fun refreshAccessToken(refreshToken: String): KtorResponse<TokenResponse> =
        client.post("v1.1/refresh") {
            header("Authorization", "Bearer $refreshToken")
        }.toKtorResponse()
    suspend fun getPagedCompanies(query: String? = null, page: Int = 1, sector: String? = null, pageSize: Int = 20): KtorResponse<com.thejawnpaul.gptinvestor.features.company.data.remote.model.AllCompanyResponse> =
        client.get("v1.1/companies") {
            if (!query.isNullOrBlank()) parameter("query", query)
            parameter("page", page)
            if (!sector.isNullOrBlank()) parameter("sector", sector)
            parameter("page_size", pageSize)
        }.toKtorResponse()
}

class KtorResponse<T>(
    val isSuccessful: Boolean,
    val body: T?,
    val errorBody: String?,
    val code: Int
)

suspend inline fun <reified T> HttpResponse.toKtorResponse(): KtorResponse<T> {
    val isSuccess = status.isSuccess()
    return KtorResponse(
        isSuccessful = isSuccess,
        body = if (isSuccess) body<T>() else null,
        errorBody = if (!isSuccess) bodyAsText() else null,
        code = status.value
    )
}
