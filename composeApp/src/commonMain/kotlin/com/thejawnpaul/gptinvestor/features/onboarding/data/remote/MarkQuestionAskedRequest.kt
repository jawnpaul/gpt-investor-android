package com.thejawnpaul.gptinvestor.features.onboarding.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarkQuestionAskedRequest(@SerialName("question") val question: String)
