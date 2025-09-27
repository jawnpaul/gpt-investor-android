package com.thejawnpaul.gptinvestor.features.conversation.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class DefaultPromptRemote(val id: String, val label: String, val query: String)
