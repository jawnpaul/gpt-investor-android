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
import retrofit2.Response

interface ApiService {
    suspend fun getCompanies(): Response<List<CompanyRemote>>

    suspend fun getCompanyFinancials(request: CompanyFinancialsRequest): Response<CompanyFinancialsRemote>

    suspend fun saveComparison(request: SaveComparisonRequest): Response<DefaultSaveResponse>

    suspend fun saveSentiment(request: SaveSentimentRequest): Response<DefaultSaveResponse>

    suspend fun getAnalystRating(request: AnalystRatingRequest): Response<AnalystRatingResponse>

    suspend fun saveIndustryRating(request: IndustryRatingRequest): Response<DefaultSaveResponse>

    suspend fun createPdf(request: DownloadPdfRequest): Response<DownloadPdfResponse>

    suspend fun getTrendingTickers(): Response<List<TrendingRemote>>

    suspend fun getDefaultPrompts(): Response<List<DefaultPromptRemote>>

    suspend fun getEntity(request: GetEntityRequest): Response<GetEntityResponse>

    suspend fun getCompanyInfo(request: CompanyDetailRemoteRequest): Response<CompanyDetailRemoteResponse>

    suspend fun getCompanyPrice(request: CompanyPriceRequest): Response<List<CompanyPriceResponse>>

    suspend fun getTopPicks(date: String): Response<List<TopPickRemote>>

    suspend fun registerToken(request: RegisterTokenRequest): Response<RegisterTokenResponse>

    suspend fun addUserToWaitlist(request: AddToWaitlistRequest): Response<AddToWaitlistResponse>
}
