package com.thejawnpaul.gptinvestor.features.company.presentation.model

import com.thejawnpaul.gptinvestor.core.utility.toReadableString

data class CompanyPresentation(
    val ticker: String,
    val name: String,
    val logo: String,
    val summary: String,
    val price: Float = 100.67f,
    val change: Float = -1.0f,
    val changeDate: Long = 1727980094120L
){
    val changeReadableDate = changeDate.toReadableString()
}
