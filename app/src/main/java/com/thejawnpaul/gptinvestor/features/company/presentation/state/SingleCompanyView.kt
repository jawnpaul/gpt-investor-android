package com.thejawnpaul.gptinvestor.features.company.presentation.state

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyDetailDefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation

data class SingleCompanyView(
    val loading: Boolean = false,
    val conversation: Conversation = CompanyDetailDefaultConversation(id = 0, response = null),
    val error: String? = null,
    val companyName: String = "",
    val inputQuery: String = ""
){
    val enableSend = inputQuery.isNotEmpty()
}
