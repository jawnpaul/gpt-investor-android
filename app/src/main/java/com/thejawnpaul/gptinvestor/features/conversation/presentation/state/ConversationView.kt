package com.thejawnpaul.gptinvestor.features.conversation.presentation.state

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation

data class ConversationView(
    val conversation: Conversation = DefaultConversation(),
    val query: String = "",
    val loading: Boolean = false
) {
    val enableSend = !loading && query.trim().isNotEmpty()
}
