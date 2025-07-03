package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.thejawnpaul.gptinvestor.core.utility.json
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
data class ConversationTitle(
    val title: String
)

class ConversationTitleParser {
    fun parseTitle(jsonString: String): ConversationTitle? {
        return try {
            json.decodeFromString<ConversationTitle>(jsonString)
        } catch (e: Exception) {
            Timber.e("Error parsing JSON: ${e.message}")
            null
        }
    }
}
