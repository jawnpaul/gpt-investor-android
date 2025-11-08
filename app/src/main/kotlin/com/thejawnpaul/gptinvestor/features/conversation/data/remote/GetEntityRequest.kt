package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetEntityRequest(val query: String)

@Serializable
data class GetEntityResponse(@SerialName("entity") val entityList: List<String> = emptyList())
