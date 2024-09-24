package com.thejawnpaul.gptinvestor.core.api

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRemote
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("companies")
    suspend fun getCompanies(): Response<List<CompanyRemote>>

    @POST("company")
    suspend fun getCompanyFinancials(@Body request: CompanyFinancialsRequest): Response<CompanyFinancialsRemote>

    @POST("save-comparison")
    suspend fun saveComparison(@Body request: SaveComparisonRequest): Response<DefaultSaveResponse>

    @POST("save-sentiment")
    suspend fun saveSentiment(@Body request: SaveSentimentRequest): Response<DefaultSaveResponse>

    @POST("get-analyst-rating")
    suspend fun getAnalystRating(@Body request: AnalystRatingRequest): Response<AnalystRatingResponse>

    @POST("save-industry-rating")
    suspend fun saveIndustryRating(@Body request: IndustryRatingRequest): Response<DefaultSaveResponse>

    @POST("create-pdf")
    suspend fun createPdf(@Body request: DownloadPdfRequest): Response<DownloadPdfResponse>

    @GET("trending-tickers")
    suspend fun getTrendingTickers(): Response<List<TrendingRemote>>

    @GET("default-prompts")
    suspend fun getDefaultPrompts(): Response<List<DefaultPromptRemote>>

    @POST("get-entity")
    suspend fun getEntity(@Body request: GetEntityRequest): Response<GetEntityResponse>

    @POST("company-info")
    suspend fun getCompanyInfo(@Body request: CompanyDetailRemoteRequest): Response<CompanyDetailRemoteResponse>
}
