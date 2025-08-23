package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.utility.json
import kotlinx.serialization.Serializable

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

    fun parseSuggestions(jsonString: String): SuggestionsResponse? {
        return try {
            json.decodeFromString<SuggestionsResponse>(jsonString)
        } catch (e: Exception) {
            Logger.e("Error parsing JSON: ${e.message}")
            null
        }
    }
}
