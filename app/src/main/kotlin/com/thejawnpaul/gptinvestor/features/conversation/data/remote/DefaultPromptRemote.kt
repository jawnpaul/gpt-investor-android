package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DefaultPromptRemote(
    @SerialName("_id") val id: DefaultPromptId,
    @SerialName("label")val label: String,
    @SerialName("query") val query: String
)

@Serializable
data class DefaultPromptId(@SerialName("\$oid") val id: String? = null)
