package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber

@Serializable
data class SuggestionsResponse(
    val suggestions: List<Suggestion>
)

@Serializable
data class Suggestion(
    val label: String,
    val query: String
)

class SuggestionParser {
    private val json = Json{
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
        prettyPrint = true
    }

    fun parseSuggestions(jsonString: String): SuggestionsResponse? {
        return try {
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            Timber.e("Error parsing JSON: ${e.message}")
            null
        }
    }
}
