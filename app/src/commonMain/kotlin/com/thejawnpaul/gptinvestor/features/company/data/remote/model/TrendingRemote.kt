package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendingRemote(@SerialName("ticker") val tickerSymbol: String, @SerialName("change") val percentageChange: Float, val name: String, val logo: String)
