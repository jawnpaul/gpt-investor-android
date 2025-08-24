package com.thejawnpaul.gptinvestor.core.api

import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.thejawnpaul.gptinvestor.core.firebase.IRemoteConfig
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiApiImpl(
    private val remoteConfig: IRemoteConfig,
) : GeminiApi {

    /**
     * To be moved later to call site
     * private val _messageFlow: MutableStateFlow<String?>
     *         get() = MutableStateFlow(null)
     *     val messageFlow: StateFlow<String?>
     *         get() = _messageFlow
     */
    private lateinit var model: GenerativeModel

    override suspend fun sendMessage(
        prompt: String,
        history: List<History>
    ): String? {
        val chat = startChat(history)
        return chat.sendMessage(prompt = prompt).text
    }

    override fun sendMessageStream(prompt: String, history: List<History>): Flow<String?> = flow {
        val chat = startChat(history)
        chat.sendMessageStream(prompt = prompt).collect { response ->
            emit(response.text)
        }
    }

    private suspend fun startChat(
        history: List<History>
    ): Chat {
        initializeApi()
        return model.startChat(history = history.map { it.toContent() })
    }

    private suspend fun initializeApi() {
        val modelName = remoteConfig.fetchAndActivateStringValue(Constants.MODEL_NAME_KEY)
        model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = modelName,
            generationConfig = generationConfig {
                temperature = Constants.TEMPERATURE_VALUE
                topP = Constants.TOP_P_VALUE
                topK = Constants.TOP_K_VALUE
                maxOutputTokens = Constants.MAX_OUTPUT_TOKENS
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.MEDIUM_AND_ABOVE)
            ),
            systemInstruction = content { text(Constants.SYSTEM_INSTRUCTIONS) }
        )
    }

    private fun History.toContent() = content(role = role) { text(text) }
}