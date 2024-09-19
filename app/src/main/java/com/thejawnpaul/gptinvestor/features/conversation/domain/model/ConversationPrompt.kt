package com.thejawnpaul.gptinvestor.features.conversation.domain.model

data class ConversationPrompt(
    val conversationId: Long = 0,
    val query: String
)
