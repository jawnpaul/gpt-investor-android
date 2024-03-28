package com.example.gptinvestor.features.company.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gptinvestor.features.company.domain.model.Company

@Entity(tableName = "company_table")
data class CompanyEntity(
    @PrimaryKey val ticker: String,
    val summary: String,
    val industry: String,
    val industryKey: String,
    val sector: String,
    val sectorKey: String,
    val country: String,
    val name: String,
    val logoUrl: String,
    val website: String,
    val date: String
) {
    fun toDomainObject() = Company(ticker = ticker, name = name, summary = summary, logo = logoUrl)
}
