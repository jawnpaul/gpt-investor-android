package com.thejawnpaul.gptinvestor.features.conversation.domain.model

data class ConversationPrompt(val conversationId: Long, val query: String, val tickerSymbol: String? = null)
