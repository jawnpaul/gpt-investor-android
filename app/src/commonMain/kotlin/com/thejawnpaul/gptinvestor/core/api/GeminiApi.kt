package com.thejawnpaul.gptinvestor.core.api

import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.History
import kotlinx.coroutines.flow.Flow

interface GeminiApi {
    suspend fun sendMessage(prompt: String, history: List<History>): String?

    fun sendMessageStream(prompt: String, history: List<History>): Flow<String?>
}