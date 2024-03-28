package com.example.gptinvestor.features.investor.domain.model

data class Company(
    val ticker: String,
    val name: String,
    val summary: String,
    val logo: String
)
