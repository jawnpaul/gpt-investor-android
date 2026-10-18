package com.thejawnpaul.gptinvestor.features.postauthonboarding.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveOnboardingAnswersRequest(
    @SerialName("experience") val experience: String?,
    @SerialName("goal") val goal: String?,
    @SerialName("risk_tolerance") val riskTolerance: String?
)
