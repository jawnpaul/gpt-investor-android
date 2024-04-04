package com.example.gptinvestor.core.api

import com.example.gptinvestor.features.company.data.remote.model.CompanyFinancialsRemote
import com.example.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.example.gptinvestor.features.company.data.remote.model.CompanyRemote
import com.example.gptinvestor.features.investor.data.remote.AnalystRatingRequest
import com.example.gptinvestor.features.investor.data.remote.AnalystRatingResponse
import com.example.gptinvestor.features.investor.data.remote.SaveComparisonRequest
import com.example.gptinvestor.features.investor.data.remote.DefaultSaveResponse
import com.example.gptinvestor.features.investor.data.remote.SaveSentimentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/companies")
    suspend fun getCompanies(): Response<List<CompanyRemote>>

    @POST("/company")
    suspend fun getCompanyFinancials(@Body request: CompanyFinancialsRequest): Response<CompanyFinancialsRemote>

    @POST("/save-comparison")
    suspend fun saveComparison(@Body request: SaveComparisonRequest): Response<DefaultSaveResponse>

    @POST("/save-sentiment")
    suspend fun saveSentiment(@Body request: SaveSentimentRequest): Response<DefaultSaveResponse>

    @GET("/get-analyst-rating")
    suspend fun getAnalystRating(@Body request: AnalystRatingRequest): Response<AnalystRatingResponse>
}
