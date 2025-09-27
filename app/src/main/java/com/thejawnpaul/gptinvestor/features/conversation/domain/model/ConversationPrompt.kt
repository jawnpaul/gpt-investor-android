package com.thejawnpaul.gptinvestor.features.conversation.domain.model

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse

data class ConversationPrompt(val conversationId: Long, val query: String)

data class CompanyPrompt(val conversationId: Long, val query: String, val company: CompanyDetailRemoteResponse? = null)
