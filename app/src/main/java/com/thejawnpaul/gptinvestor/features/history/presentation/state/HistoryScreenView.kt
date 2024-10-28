package com.thejawnpaul.gptinvestor.features.history.presentation.state

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation

data class HistoryScreenView(
    val loading: Boolean = false,
    val list: List<StructuredConversation> = emptyList(),
    val error: String? = null
)
