package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    @SerialName("query") val query: String,
    @SerialName("history") val history: List<ChatMessageRemote> = emptyList()
)

@Serializable
data class AiChatRequest(
    @SerialName("prompt") val prompt: String,
    @SerialName("conversation_id") val conversationId: String? = null,
    @SerialName("entity") val tickerSymbol: String? = null
)

@Serializable
data class ChatMessageRemote(
    @SerialName("role") val role: String, // "user" or "model"
    @SerialName("text") val text: String
)

@Serializable
data class SuggestionResponse(@SerialName("suggestions") val suggestions: List<SuggestionRemote> = emptyList())

@Serializable
data class SuggestionRemote(@SerialName("label") val label: String?, @SerialName("query") val query: String?)

@Serializable
data class ConversationTitleResponse(@SerialName("title") val title: String)

@Serializable
data class TextStreamResponse(@SerialName("text") val text: String)

@Serializable
data class ConversationIdResponse(@SerialName("id") val id: String)

@Serializable
data class CompletionResponse(@SerialName("done") val done: Boolean)

@Serializable
data class ErrorResponse(@SerialName("error") val error: String)
