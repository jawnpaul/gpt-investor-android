package com.thejawnpaul.gptinvestor.core.api

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRemote
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyPriceRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyPriceResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyRemote
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.TrendingRemote
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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("v1/companies")
    suspend fun getCompanies(): Response<List<CompanyRemote>>

    @POST("v1/company")
    suspend fun getCompanyFinancials(@Body request: CompanyFinancialsRequest): Response<CompanyFinancialsRemote>

    @POST("v1/save-comparison")
    suspend fun saveComparison(@Body request: SaveComparisonRequest): Response<DefaultSaveResponse>

    @POST("v1/save-sentiment")
    suspend fun saveSentiment(@Body request: SaveSentimentRequest): Response<DefaultSaveResponse>

    @POST("v1/get-analyst-rating")
    suspend fun getAnalystRating(@Body request: AnalystRatingRequest): Response<AnalystRatingResponse>

    @POST("v1/save-industry-rating")
    suspend fun saveIndustryRating(@Body request: IndustryRatingRequest): Response<DefaultSaveResponse>

    @POST("v1/create-pdf")
    suspend fun createPdf(@Body request: DownloadPdfRequest): Response<DownloadPdfResponse>

    @GET("v1/trending-tickers")
    suspend fun getTrendingTickers(): Response<List<TrendingRemote>>

    @GET("v1/default-prompts")
    suspend fun getDefaultPrompts(): Response<List<DefaultPromptRemote>>

    @POST("v1/get-entity")
    suspend fun getEntity(@Body request: GetEntityRequest): Response<GetEntityResponse>

    @POST("v1/company-info")
    suspend fun getCompanyInfo(@Body request: CompanyDetailRemoteRequest): Response<CompanyDetailRemoteResponse>

    @POST("v1/company-price")
    suspend fun getCompanyPrice(@Body request: CompanyPriceRequest): Response<List<CompanyPriceResponse>>

    @GET("v1.1/top-picks")
    suspend fun getTopPicks(@Query("date") date: String): Response<List<TopPickRemote>>

    @POST("v1/notifications/register-token")
    suspend fun registerToken(@Body request: RegisterTokenRequest): Response<RegisterTokenResponse>
}
