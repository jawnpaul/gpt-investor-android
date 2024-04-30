package com.thejawnpaul.gptinvestor.features.investor.data.remote

import com.squareup.moshi.Json

data class DownloadPdfRequest(
    @field:Json(name = "name")val name: String,
    @field:Json(name = "about")val about: String,
    @field:Json(name = "similar_companies")val similarCompanies: String,
    @field:Json(name = "comparison")val comparison: String,
    @field:Json(name = "sentiment")val sentiment: String,
    @field:Json(name = "analyst_rating")val analystRating: String,
    @field:Json(name = "industry_rating")val industryRating: String,
    @field:Json(name = "final_rating")val finalRating: String,
    @field:Json(name = "open")val open: String,
    @field:Json(name = "high")val high: String,
    @field:Json(name = "low")val low: String,
    @field:Json(name = "close")val close: String,
    @field:Json(name = "volume")val volume: String,
    @field:Json(name = "market_cap")val marketCap: String
)

data class DownloadPdfResponse(
    @field:Json(name = "url")val url: String
)
