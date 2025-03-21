package com.thejawnpaul.gptinvestor.features.toppick.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "top_picks_table")
data class TopPickEntity(
    @PrimaryKey
    val id: String,
    val companyName: String,
    val ticker: String,
    val rationale: String,
    val metrics: List<String>,
    val risks: List<String>,
    val confidenceScore: Int,
    val isSaved: Boolean = false,
    val date: String
)
