package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.core.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class ConversationRepository @Inject constructor(
    private val apiService: ApiService,
    private val analyticsLogger: AnalyticsLogger
) :
    IConversationRepository {

    private var conversationId: Long = 0L

    private val database = mutableMapOf<Long, Conversation>()

    private val newModel = "gemini-1.5-pro-latest"
    private val oldModel = "gemini-1.0-pro"
    private val flashModel = "gemini-1.5-flash"

    private val generativeModel = GenerativeModel(
        modelName = flashModel,
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.2f
            topK = 1
            topP = 1f
            maxOutputTokens = 1024
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        ),
        systemInstruction = content { text(Constants.SYSTEM_INSTRUCTIONS) }
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

    override suspend fun getDefaultPromptResponse(prompt: DefaultPrompt): Flow<Either<Failure, Conversation>> = flow {
        try {
            // Create a new conversation object, get its id, use the id as Conversation_id

            val chunk = StringBuilder()

            analyticsLogger.logDefaultPromptSelected(
                promptTitle = prompt.title,
                promptQuery = prompt.query
            )

            val structuredConversation = StructuredConversation(
                id = conversationId,
                title = prompt.title,
                messageList = mutableListOf(
                    GenAiMessage(query = prompt.query, loading = true)
                )
            )
            emit(Either.Right(structuredConversation))

            val response = generativeModel.generateContentStream(prompt = prompt.query)
            response.collect { result ->
                result.text?.let { responseText ->

                    chunk.append(responseText)

                    val lastIndex = structuredConversation.messageList.lastIndex
                    val last = structuredConversation.messageList[lastIndex].copy(
                        query = prompt.query,
                        response = chunk.toString(),
                        loading = false
                    )
                    structuredConversation.messageList[lastIndex] = last

                    emit(Either.Right(structuredConversation))

                    database[conversationId] = structuredConversation
                }
            }

                /*val response = model.generateContent(prompt.query)
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
                }*/
        } catch (e: Exception) {
            when (e) {
                is GoogleGenerativeAIException -> {
                    emit(Either.Left(GenAIException()))
                    Timber.e(e.stackTraceToString())
                }

                else -> {
                    Timber.e(e.stackTraceToString())
                    emit(Either.Left(Failure.ServerError))
                }
            }
        }
    }

    override suspend fun getInputResponse(prompt: ConversationPrompt): Flow<Either<Failure, Conversation>> = flow {
        try {
            // if there's an existing conversation, append new message else create a new conversation
            if (database.containsKey(prompt.conversationId)) {
                Timber.e("Existing conversation")
                val chunk = StringBuilder()

                // get the previous messages in the conversation, add as context
                val conv = database[prompt.conversationId] as StructuredConversation
                val prevMessageList = conv.messageList

                val newMessageId = prevMessageList.last().id + 1
                val message =
                    GenAiMessage(id = newMessageId, query = prompt.query, loading = true)

                prevMessageList.add(message)

                emit(Either.Right(conv))

                val model = generativeModel.apply {
                    content {
                        text(Constants.SYSTEM_INSTRUCTIONS)
                    }
                }

                val response = model.generateContentStream(prompt.query)
                response.collect { result ->
                    result.text?.let { responseText ->

                        chunk.append(responseText)

                        val lastIndex = conv.messageList.lastIndex
                        val last = conv.messageList[lastIndex].copy(
                            query = prompt.query,
                            response = chunk.toString(),
                            loading = false
                        )
                        conv.messageList[lastIndex] = last

                        emit(Either.Right(conv))

                        database[conversationId] = conv
                    }
                }
            } else {
                Timber.e("No existing conversation")
                val chunk = StringBuilder()

                val structuredConversation = StructuredConversation(
                    id = conversationId,
                    title = "Default title",
                    messageList = mutableListOf(
                        GenAiMessage(query = prompt.query, loading = true)
                    )
                )
                emit(Either.Right(structuredConversation))

                val model = generativeModel.apply {
                    content {
                        text(Constants.SYSTEM_INSTRUCTIONS)
                    }
                }

                val response = model.generateContentStream(prompt = prompt.query)
                response.collect { result ->
                    result.text?.let { responseText ->

                        chunk.append(responseText)

                        val lastIndex = structuredConversation.messageList.lastIndex
                        val last = structuredConversation.messageList[lastIndex].copy(
                            query = prompt.query,
                            response = chunk.toString(),
                            loading = false
                        )
                        structuredConversation.messageList[lastIndex] = last

                        emit(Either.Right(structuredConversation))

                        database[conversationId] = structuredConversation
                    }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is GoogleGenerativeAIException -> {
                    Timber.e(e.stackTraceToString())
                    emit(Either.Left(GenAIException()))
                }

                else -> {
                    Timber.e(e.stackTraceToString())
                    emit(Either.Left(Failure.ServerError))
                }
            }
        }
    }
}
