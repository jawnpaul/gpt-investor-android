package com.thejawnpaul.gptinvestor.features.conversation.domain.model

import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity

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

data class GenAiTextMessage(
    override val id: Long = 0,
    val query: String,
    override val response: String? = null,
    override val loading: Boolean = true
) : GenAiMessage

sealed interface GenAiMessage {
    val id: Long
    val response: String?
    val loading: Boolean
}

data class GenAiEntityMessage(override val id: Long = 1, val entity: CompanyEntity? = null) :
    GenAiMessage {
    override val response: String
        get() = ""
    override val loading: Boolean
        get() = true
}
