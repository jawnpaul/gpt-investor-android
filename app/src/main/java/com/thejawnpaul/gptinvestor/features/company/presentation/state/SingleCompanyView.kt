package com.thejawnpaul.gptinvestor.features.company.presentation.state

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyDetailDefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation

data class SingleCompanyView(
    val loading: Boolean = false,
    val conversation: Conversation = CompanyDetailDefaultConversation(id = 0, response = null),
    val error: String? = null,
    val companyName: String = "",
    val inputQuery: String = "",
    val header: CompanyHeaderPresentation = CompanyHeaderPresentation(),
    val genText: String = ""
) {
    val enableSend = inputQuery.trim().isNotEmpty()
}

data class CompanyHeaderPresentation(
    val companyTicker: String = "",
    val companyLogo: String = "",
    val price: Float = 0f,
    val percentageChange: Float = 0f,
    val companyName: String = ""
)
