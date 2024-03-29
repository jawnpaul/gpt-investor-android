package com.example.gptinvestor.core.api

import com.example.gptinvestor.features.company.data.remote.model.CompanyFinancialsRemote
import com.example.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.example.gptinvestor.features.company.data.remote.model.CompanyRemote
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/companies")
    suspend fun getCompanies(): Response<List<CompanyRemote>>

    @POST("/company")
    suspend fun getCompanyFinancials(@Body request: CompanyFinancialsRequest): Response<CompanyFinancialsRemote>
}
