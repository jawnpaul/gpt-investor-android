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
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.AllTidbitResponse
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitBookmarkRequest
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitBookmarkResponse
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitLikeRequest
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitLikeResponse
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitRemote
import com.thejawnpaul.gptinvestor.features.toppick.data.remote.TopPickRemote
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface ApiService {
    @GET("v1/companies")
    suspend fun getCompanies(): List<CompanyRemote>

    @Headers("Content-Type: application/json")
    @POST("v1/company")
    suspend fun getCompanyFinancials(@Body request: CompanyFinancialsRequest): CompanyFinancialsRemote

    @Headers("Content-Type: application/json")
    @POST("v1/save-comparison")
    suspend fun saveComparison(@Body request: SaveComparisonRequest): DefaultSaveResponse

    @Headers("Content-Type: application/json")
    @POST("v1/save-sentiment")
    suspend fun saveSentiment(@Body request: SaveSentimentRequest): DefaultSaveResponse

    @Headers("Content-Type: application/json")
    @POST("v1/get-analyst-rating")
    suspend fun getAnalystRating(@Body request: AnalystRatingRequest): AnalystRatingResponse

    @Headers("Content-Type: application/json")
    @POST("v1/save-industry-rating")
    suspend fun saveIndustryRating(@Body request: IndustryRatingRequest): DefaultSaveResponse

    @Headers("Content-Type: application/json")
    @POST("v1/create-pdf")
    suspend fun createPdf(@Body request: DownloadPdfRequest): DownloadPdfResponse

    @GET("v1/trending-tickers")
    suspend fun getTrendingTickers(): List<TrendingRemote>

    @GET("v1/default-prompts")
    suspend fun getDefaultPrompts(): List<DefaultPromptRemote>

    @Headers("Content-Type: application/json")
    @POST("v1/get-entity")
    suspend fun getEntity(@Body request: GetEntityRequest): GetEntityResponse

    @Headers("Content-Type: application/json")
    @POST("v1/company-info")
    suspend fun getCompanyInfo(@Body request: CompanyDetailRemoteRequest): CompanyDetailRemoteResponse

    @Headers("Content-Type: application/json")
    @POST("v1/company-price")
    suspend fun getCompanyPrice(@Body request: CompanyPriceRequest): List<CompanyPriceResponse>

    @GET("v1.1/top-picks")
    suspend fun getTopPicks(@Query("date") date: String): List<TopPickRemote>

    @Headers("Content-Type: application/json")
    @POST("v1/notifications/register-token")
    suspend fun registerToken(@Body request: RegisterTokenRequest): RegisterTokenResponse

    @Headers("Content-Type: application/json")
    @POST("v1/add-to-waitlist")
    suspend fun addUserToWaitlist(@Body request: AddToWaitlistRequest): AddToWaitlistResponse

    @GET("v1/tidbit/all-tidbit")
    suspend fun getAllTidbit(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10,
        @Query("user_id") userId: String
    ): AllTidbitResponse

    @GET("v1/tidbit/latest-tidbits")
    suspend fun getLatestTidbits(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10,
        @Query("user_id") userId: String
    ): AllTidbitResponse

    @GET("v1/tidbit/trending-tidbits")
    suspend fun getTrendingTidbit(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10,
        @Query("user_id") userId: String
    ): AllTidbitResponse

    @GET("v1/tidbit/bookmarked-tidbits")
    suspend fun getSavedTidbits(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10,
        @Query("user_id") userId: String
    ): AllTidbitResponse

    @GET("v1/tidbit/today-tidbit")
    suspend fun getTodayTidbit(@Query("user_id") userId: String): TidbitRemote

    @GET("v1/tidbit/single-tidbit")
    suspend fun getSingleTidbit(
        @Query("id") id: String,
        @Query("user_id") userId: String
    ): TidbitRemote

    @Headers("Content-Type: application/json")
    @POST("v1/tidbit/like-tidbit")
    suspend fun likeTidbit(@Body request: TidbitLikeRequest): TidbitLikeResponse

    @Headers("Content-Type: application/json")
    @POST("v1/tidbit/unlike-tidbit")
    suspend fun unlikeTidbit(@Body request: TidbitLikeRequest): TidbitLikeResponse

    @Headers("Content-Type: application/json")
    @POST("v1/tidbit/bookmark-tidbit")
    suspend fun bookmarkTidbit(@Body request: TidbitBookmarkRequest): TidbitBookmarkResponse

    @Headers("Content-Type: application/json")
    @POST("v1/tidbit/unbookmark-tidbit")
    suspend fun removeBookmark(@Body request: TidbitBookmarkRequest): TidbitBookmarkResponse
}
