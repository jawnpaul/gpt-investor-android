package com.thejawnpaul.gptinvestor.features.company.presentation.state

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyDetailDefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel

data class SingleCompanyView(
    val loading: Boolean = false,
    val conversation: Conversation = CompanyDetailDefaultConversation(id = 0, response = null),
    val error: String? = null,
    val companyName: String = "",
    val inputQuery: String = "",
    val header: CompanyHeaderPresentation = CompanyHeaderPresentation(),
    val genText: String = "",
    val availableModels: List<AvailableModel> = emptyList(),
    val selectedModel: AvailableModel = DefaultModel(),
    val waitlistAvailableOptions: List<String> = listOf(
        "Advanced analysis",
        "Personalized insights",
        "Risk awareness",
        "Actionable guidance",
        "Transparent data sources",
        "Unlimited queries"
    ),
    val selectedWaitlistOptions: List<String> = emptyList(),
    val showWaitListBottomSheet: Boolean = false
) {
    val enableSend = inputQuery.trim().isNotEmpty()
}

data class CompanyHeaderPresentation(val companyTicker: String = "", val companyLogo: String = "", val price: Float = 0f, val percentageChange: Float = 0f, val companyName: String = "")
