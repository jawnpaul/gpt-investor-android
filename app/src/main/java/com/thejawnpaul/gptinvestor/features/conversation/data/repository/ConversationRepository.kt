package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class ConversationRepository @Inject constructor(private val apiService: ApiService) :
    IConversationRepository {
    private val newModel = "gemini-1.5-pro-latest"
    private val oldModel = "gemini-1.0-pro"
    private val generativeModel = GenerativeModel(
        modelName = newModel,
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.2f
            topK = 1
            topP = 1f
            maxOutputTokens = 2048
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    override suspend fun getDefaultPrompts(): Flow<Either<Failure, List<DefaultPrompt>>> = flow {
        try {
            val response = apiService.getDefaultPrompts()
            if (response.isSuccessful) {
                response.body()?.let { prompts ->
                    val defaultPrompts = prompts.map { prompt ->
                        with(prompt) {
                            DefaultPrompt(title = label, query = query)
                        }
                    }.take(8)
                    emit(Either.Right(defaultPrompts))
                }
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getDefaultPromptResponse(prompt: DefaultPrompt): Flow<Either<Failure, Conversation>> =
        flow {
            try {
                val structuredConversation = StructuredConversation(
                    id = 0,
                    title = prompt.title,
                    messageList = mutableListOf(
                        GenAiMessage(query = prompt.query, loading = true)
                    )
                )
                emit(Either.Right(structuredConversation))

                val model = generativeModel.apply {
                    content {
                        text(
                            "You are an AI assistant specializing in investment analysis. Provide concise, " +
                                    "direct answers focused on key points. Avoid unnecessary elaboration. Prioritize accuracy and relevance. " +
                                    "If uncertain, state it clearly. For complex topics, offer a brief overview followed by key bullet points. " +
                                    "Use data and facts when available. Tailor your language to be accessible to both novice and experienced investors."
                        )
                    }
                }

                val response = model.generateContent(prompt.query)
                response.text?.let { responseText ->
                    // create conversation object

                    val lastIndex = structuredConversation.messageList.lastIndex
                    val last = structuredConversation.messageList[lastIndex].copy(
                        query = prompt.query,
                        response = responseText,
                        loading = false
                    )
                    structuredConversation.messageList[lastIndex] = last

                    emit(Either.Right(structuredConversation))
                }
            } catch (e: Exception) {
                when (e) {
                    is GoogleGenerativeAIException -> {
                        Timber.e(e.stackTraceToString())
                    }

                    else -> {
                        Timber.e(e.stackTraceToString())
                        emit(Either.Left(Failure.ServerError))
                    }
                }
            }
        }
}
