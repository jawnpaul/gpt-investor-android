package com.thejawnpaul.gptinvestor.features.conversation.domain.model

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.repository.Suggestion

sealed interface Conversation {

    val id: Long
}

data class StructuredConversation(
    override val id: Long,
    val title: String,
    val messageList: MutableList<GenAiMessage> = mutableListOf(),
    val suggestedPrompts: List<Suggestion> = emptyList()
) : Conversation

data class UnStructuredConversation(override val id: Long) : Conversation

data class DefaultConversation(
    override val id: Long = 0,
    val prompts: List<DefaultPrompt> = emptyList()
) : Conversation

data class CompanyDetailDefaultConversation(
    override val id: Long,
    val response: CompanyDetailRemoteResponse? = null
) : Conversation

data class GenAiTextMessage(
    override val id: Long = 0,
    val query: String,
    override val response: String? = null,
    override val loading: Boolean = false
) : GenAiMessage

sealed interface GenAiMessage {
    val id: Long
    val response: String?
    val loading: Boolean
}

data class GenAiEntityMessage(
    override val id: Long = 1,
    val entity: CompanyDetailRemoteResponse? = null
) :
    GenAiMessage {
    override val response: String
        get() = ""
    override val loading: Boolean
        get() = false
}
