package com.thejawnpaul.gptinvestor.features.conversation.domain.model

sealed interface Conversation {

    val id: Long
}

data class StructuredConversation(override val id: Long) : Conversation

data class UnStructuredConversation(override val id: Long) : Conversation

data class DefaultConversation(
    override val id: Long = 0,
    val prompts: List<DefaultPrompt> = emptyList()
) : Conversation
