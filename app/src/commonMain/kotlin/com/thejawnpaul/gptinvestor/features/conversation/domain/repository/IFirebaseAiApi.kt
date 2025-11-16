package com.thejawnpaul.gptinvestor.features.conversation.domain.repository

import kotlinx.coroutines.flow.Flow

interface IFirebaseAiApi {

    suspend fun sendMessage(history: List<HistoryContent>, prompt: String): String?

    fun sendMessageStream(history: List<HistoryContent>, prompt: String): Flow<String?>
}

data class HistoryContent(
    val role: String,
    val message: String
)