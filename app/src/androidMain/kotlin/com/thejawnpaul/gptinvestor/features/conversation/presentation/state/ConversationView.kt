package com.thejawnpaul.gptinvestor.features.conversation.presentation.state

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel

data class ConversationView(
    val conversation: Conversation = DefaultConversation(),
    val query: String = "",
    val loading: Boolean = false,
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
    val enableSend = !loading && query.trim().isNotEmpty()
}
