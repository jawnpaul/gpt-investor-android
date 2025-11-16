package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.thejawnpaul.gptinvestor.core.firebase.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.HistoryContent
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IFirebaseAiApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseAiApi(remoteConfig: RemoteConfig): IFirebaseAiApi {
    private val generativeModel = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = remoteConfig.fetchAndActivateStringValue(Constants.MODEL_NAME_KEY),
        generationConfig = generationConfig {
            temperature = 0.2f
            topK = 1
            topP = 1f
            maxOutputTokens = 1024
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.MEDIUM_AND_ABOVE)
        ),
        systemInstruction = content { text(Constants.SYSTEM_INSTRUCTIONS) }
    )

    private fun startChat(history: List<HistoryContent>): Chat {
        val chatHistory = history.map { it.toContent() }
        return generativeModel.startChat(chatHistory)
    }

    override suspend fun sendMessage(history: List<HistoryContent>, prompt: String): String? {
        val chat = startChat(history)
        return chat.sendMessage(prompt = prompt).text
    }

    override fun sendMessageStream(history: List<HistoryContent>, prompt: String): Flow<String?> {
        val chat = startChat(history)
        return chat.sendMessageStream(prompt).map { it.text }
    }

    private fun HistoryContent.toContent(): Content =
        content(role = role) { text(message) }
}