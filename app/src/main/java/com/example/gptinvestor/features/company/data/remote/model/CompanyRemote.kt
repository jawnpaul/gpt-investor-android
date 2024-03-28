package com.example.gptinvestor.features.company.data.remote.model

import com.example.gptinvestor.features.company.domain.model.Company
import com.squareup.moshi.Json

data class CompanyRemote(
    @field:Json(name = "ticker") val ticker: String,
    @field:Json(name = "summary") val summary: String,
    @field:Json(name = "industry") val industry: String,
    @field:Json(name = "industry_key") val industryKey: String,
    @field:Json(name = "sector") val sector: String,
    @field:Json(name = "sector_key") val sectorKey: String,
    @field:Json(name = "country") val country: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "logo_url") val logoUrl: String,
    @field:Json(name = "website") val website: String,
    @field:Json(name = "date") val date: String
) {
    fun toDomainObject() = Company(ticker = ticker, name = name, logo = logoUrl, summary = summary)
}
