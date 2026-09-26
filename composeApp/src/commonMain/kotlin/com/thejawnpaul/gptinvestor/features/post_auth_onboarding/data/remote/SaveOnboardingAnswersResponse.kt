package com.thejawnpaul.gptinvestor.features.postauthonboarding.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveOnboardingAnswersResponse(@SerialName("message") val message: String)
