package com.example.gptinvestor.core.api

import com.example.gptinvestor.features.company.data.remote.model.CompanyRemote
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/companies")
    suspend fun getCompanies(): Response<List<CompanyRemote>>
}
