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


interface ApiService {
    suspend fun getCompanies(): Result<List<CompanyRemote>>

    suspend fun getCompanyFinancials(request: CompanyFinancialsRequest): Result<CompanyFinancialsRemote>

    suspend fun saveComparison(request: SaveComparisonRequest): Result<DefaultSaveResponse>

    suspend fun saveSentiment(request: SaveSentimentRequest): Result<DefaultSaveResponse>

    suspend fun getAnalystRating(request: AnalystRatingRequest): Result<AnalystRatingResponse>

    suspend fun saveIndustryRating(request: IndustryRatingRequest): Result<DefaultSaveResponse>

    suspend fun createPdf(request: DownloadPdfRequest): Result<DownloadPdfResponse>

    suspend fun getTrendingTickers(): Result<List<TrendingRemote>>

    suspend fun getDefaultPrompts(): Result<List<DefaultPromptRemote>>

    suspend fun getEntity(request: GetEntityRequest): Result<GetEntityResponse>

    suspend fun getCompanyInfo(request: CompanyDetailRemoteRequest): Result<CompanyDetailRemoteResponse>

    suspend fun getCompanyPrice(request: CompanyPriceRequest): Result<List<CompanyPriceResponse>>

    suspend fun getTopPicks(date: String): Result<List<TopPickRemote>>

    suspend fun registerToken(request: RegisterTokenRequest): Result<RegisterTokenResponse>

    suspend fun addUserToWaitlist(request: AddToWaitlistRequest): Result<AddToWaitlistResponse>
}
