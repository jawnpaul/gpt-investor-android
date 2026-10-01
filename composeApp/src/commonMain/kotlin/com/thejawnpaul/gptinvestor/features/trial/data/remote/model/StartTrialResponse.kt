package com.thejawnpaul.gptinvestor.features.trial.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartTrialResponse(
    @SerialName("plan") val plan: String? = null,
    @SerialName("source") val source: String? = null,
    @SerialName("trial_expires_at") val trialExpiresAt: String? = null
)
