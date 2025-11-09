package com.thejawnpaul.gptinvestor.features.investor.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DownloadPdfRequest(
    val name: String,
    val about: String,
    @SerialName("similar_companies") val similarCompanies: String,
    val comparison: String,
    val sentiment: String,
    @SerialName("analyst_rating") val analystRating: String,
    @SerialName("industry_rating") val industryRating: String,
    @SerialName("final_rating") val finalRating: String,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    @SerialName("market_cap") val marketCap: String
)

@Serializable
data class DownloadPdfResponse(val url: String)
