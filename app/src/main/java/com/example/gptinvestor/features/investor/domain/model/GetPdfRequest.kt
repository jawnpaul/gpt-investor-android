package com.example.gptinvestor.features.investor.domain.model

data class GetPdfRequest(
    val ticker:String,
    val finalRating: String,
    val similarCompanies: String,
    val sentiment:String,
    val comparison:String,
    val analystRating: String,
    val industryRating: String,
    val open: String,
    val high:String,
    val low: String,
    val close:String,
    val volume:String,
    val marketCap:String
)