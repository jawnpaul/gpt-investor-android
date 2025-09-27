package com.thejawnpaul.gptinvestor.features.history.presentation.state

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation

data class HistoryScreenView(val loading: Boolean = false, val list: Map<String, List<StructuredConversation>> = emptyMap(), val error: String? = null)
