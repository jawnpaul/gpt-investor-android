package com.thejawnpaul.gptinvestor.features.conversation.domain.repository

import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow

interface IFirebaseAiApi {

    suspend fun sendMessage(history: List<Content>, prompt: String): GenerateContentResponse

    fun sendMessageStream(history: List<Content>, prompt: String): Flow<GenerateContentResponse>
}