package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import co.touchlab.kermit.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
        Logger.e("Error parsing JSON: ${e.message}")
        null
    }
}
