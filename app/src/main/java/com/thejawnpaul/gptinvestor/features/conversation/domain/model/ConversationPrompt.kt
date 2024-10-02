package com.thejawnpaul.gptinvestor.features.conversation.domain.model

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse

data class ConversationPrompt(
    val conversationId: Long = 0,
    val query: String
)

data class CompanyPrompt(
    val conversationId: Long = 0L,
    val query: String,
    val company: CompanyDetailRemoteResponse? = null
)
