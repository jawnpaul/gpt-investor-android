package com.thejawnpaul.gptinvestor.features.conversation.domain.model

sealed interface Conversation {

    val id: Long
}

data class StructuredConversation(
    override val id: Long,
    val title: String,
    val messageList: MutableList<GenAiMessage> = mutableListOf()
) : Conversation

data class UnStructuredConversation(override val id: Long) : Conversation

data class DefaultConversation(
    override val id: Long = 0,
    val prompts: List<DefaultPrompt> = emptyList()
) : Conversation

data class GenAiMessage(val query: String, val response: String? = null, val loading: Boolean = true)
