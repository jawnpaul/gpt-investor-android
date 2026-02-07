package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatRequest(
    @field:Json(name = "query") val query: String,
    @field:Json(name = "history") val history: List<ChatMessageRemote> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AiChatRequest(
    @field:Json(name = "prompt") val prompt: String,
    @field:Json(name = "models") val models: List<String>? = listOf(
        "arcee-ai/trinity-mini:free",
        "qwen/qwen3-4b:free"
    ),
    @field:Json(name = "conversation_id") val conversationId: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatMessageRemote(
    @field:Json(name = "role") val role: String, // "user" or "model"
    @field:Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class SuggestionResponse(
    @field:Json(name = "suggestions") val suggestions: List<SuggestionRemote> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SuggestionRemote(
    @field:Json(name = "label") val label: String?,
    @field:Json(name = "query") val query: String?
)

@JsonClass(generateAdapter = true)
data class ConversationTitleResponse(
    @field:Json(name = "title") val title: String
)

@JsonClass(generateAdapter = true)
data class TextStreamResponse(
    @field:Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class ConversationIdResponse(
    @field:Json(name = "id") val id: String
)

@JsonClass(generateAdapter = true)
data class CompletionResponse(
    @field:Json(name = "done") val done: Boolean
)

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @field:Json(name = "error") val error: String
)
