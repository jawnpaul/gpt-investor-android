package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber

@Serializable
data class ConversationTitle(val title: String)

class ConversationTitleParser {

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
        prettyPrint = true
    }

    fun parseTitle(jsonString: String): ConversationTitle? = try {
        json.decodeFromString(jsonString)
    } catch (e: Exception) {
        Timber.e("Error parsing JSON: ${e.message}")
        null
    }
}
