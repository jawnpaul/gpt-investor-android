package com.thejawnpaul.gptinvestor.features.conversation.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class DefaultPrompt(
    val title: String,
    val query: String
)

@Serializable
data class RemoteConfigPrompt(
    @SerialName("_id") val id: RemoteConfigId? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("query") val query: String? = null
)

@Serializable
data class RemoteConfigId(
    @SerialName("\$oid") val id: String? = null
)

class DefaultPromptParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseDefaultPrompts(jsonString: String): List<RemoteConfigPrompt>? {
        return try {
            json.decodeFromString<List<RemoteConfigPrompt>>(jsonString)
        } catch (e: Exception) {
            println("Error parsing JSON: $jsonString")
            e.printStackTrace()
            null
        }
    }
}

