package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.AiChatRequest
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.ConversationIdResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.ConversationTitleResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.ErrorResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.SuggestionRemote
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.SuggestionResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.TextStreamResponse
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPromptParser
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Singleton
import timber.log.Timber

@Singleton(binds = [IConversationRepository::class])
class ConversationRepository(
    private val apiService: KtorApiService,
    private val analyticsLogger: AnalyticsLogger,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val remoteConfig: RemoteConfig
) : IConversationRepository {

    private val json = Json { ignoreUnknownKeys = true }


    override suspend fun getDefaultPrompts(): Flow<Either<Failure, List<DefaultPrompt>>> = flow {
        try {
            /*val response = apiService.getDefaultPrompts()
            if (response.isSuccessful) {
                response.body()?.let { prompts ->
                    val defaultPrompts = prompts.map { prompt ->
                        with(prompt) {
                            DefaultPrompt(title = label, query = query)
                        }
                    }.take(8)
                    emit(Either.Right(defaultPrompts))
                }
            }*/
            val defaultPromptsString =
                remoteConfig.fetchAndActivateStringValue(Constants.DEFAULT_PROMPTS_VERSION)
            val parser = DefaultPromptParser()
            val remoteConfigDefaultPrompt =
                parser.parseDefaultPrompts(defaultPromptsString)?.shuffled()?.take(8)
            val defaultPrompt = remoteConfigDefaultPrompt?.map { prompt ->
                with(prompt) {
                    DefaultPrompt(title = label ?: "", query = query ?: "")
                }
            } ?: emptyList()

            emit(Either.Right(defaultPrompt))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getDefaultPromptResponse(prompt: DefaultPrompt): Flow<Either<Failure, Conversation>> = flow {
        var structuredConversation: StructuredConversation? = null
        var messageId: Long? = null
        try {
            analyticsLogger.logEvent(
                eventName = "Default Prompt Selected",
                params = mapOf("prompt_title" to prompt.title, "prompt_query" to prompt.query)
            )

            // Always create a new conversation entity
            val conversationId = conversationDao.insertConversation(
                ConversationEntity(
                    title = prompt.title,
                    createdAt = System.currentTimeMillis()
                )
            )

            messageId = messageDao.insertMessage(
                MessageEntity(
                    conversationId = conversationId,
                    query = prompt.query,
                    createdAt = System.currentTimeMillis()
                )
            )

            structuredConversation = StructuredConversation(
                id = conversationId,
                title = prompt.title,
                messageList = mutableListOf(
                    GenAiTextMessage(
                        id = messageId,
                        query = prompt.query,
                        loading = true,
                        feedbackStatus = 0
                    )
                )
            )
            emit(Either.Right(structuredConversation))

            val aiChatRequest = AiChatRequest(prompt = prompt.query)
            apiService.chatAiResponse(aiChatRequest) { chatResponse ->
                if (chatResponse.status.value in 200..299) {
                    handleSseStream(
                        response = chatResponse,
                        initialConversation = structuredConversation,
                        prompt = prompt.query,
                        messageId = messageId
                    )
                } else {
                    structuredConversation.let { conversation ->
                        val updatedMessages = ArrayList(conversation.messageList)
                        val index = updatedMessages.indexOfFirst { it.id == messageId }
                        if (index != -1) {
                            val original = updatedMessages[index] as GenAiTextMessage
                            updatedMessages[index] = original.copy(
                                loading = false,
                                response = original.response ?: "Couldn't generate a response"
                            )
                            emit(Either.Right(conversation.copy(messageList = updatedMessages)))
                        }
                    }
                    emit(Either.Left(mapHttpCodeToFailure(chatResponse.status.value)))
                }
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            structuredConversation?.let { conversation ->
                val updatedMessages = ArrayList(conversation.messageList)
                val index = updatedMessages.indexOfFirst { it.id == messageId }
                if (index != -1) {
                    val original = updatedMessages[index] as GenAiTextMessage
                    updatedMessages[index] = original.copy(
                        loading = false,
                        response = original.response ?: "Couldn't generate a response"
                    )
                    emit(Either.Right(conversation.copy(messageList = updatedMessages)))
                }
            }
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getInputResponse(prompt: ConversationPrompt): Flow<Either<Failure, Conversation>> = flow {
        var currentConversation: StructuredConversation? = null
        var newMessageId: Long? = null
        try {
            // Use 'var' to allow reassignment of the conversation object
            currentConversation = getConversation(prompt)
            Timber.e(currentConversation.id.toString())

            newMessageId =
                messageDao.insertMessage(
                    MessageEntity(
                        conversationId = currentConversation.id,
                        createdAt = System.currentTimeMillis(),
                        query = prompt.query
                    )
                )

            // Ensure we are modifying a mutable list or creating new lists when copying
            val messagesWithNewQuery = ArrayList(currentConversation?.messageList ?: emptyList())
            messagesWithNewQuery.add(
                GenAiTextMessage(
                    id = newMessageId,
                    query = prompt.query,
                    loading = true,
                    feedbackStatus = 0
                )
            )
            currentConversation = currentConversation.copy(messageList = messagesWithNewQuery)
            emit(Either.Right(currentConversation))

            val entity = conversationDao.getSingleConversation(currentConversation.id)
            val aiChatRequest = AiChatRequest(
                prompt = prompt.query,
                conversationId = entity?.remoteId,
                tickerSymbol = prompt.tickerSymbol
            )
            apiService.chatAiResponse(aiChatRequest) { chatResponse ->
                if (chatResponse.status.value in 200..299) {
                    handleSseStream(
                        chatResponse,
                        currentConversation,
                        prompt.query,
                        newMessageId ?: -1L
                    )
                } else {
                    currentConversation.let { conversation ->
                        val updatedMessages = ArrayList(conversation.messageList)
                        val index = updatedMessages.indexOfFirst { it.id == newMessageId }
                        if (index != -1) {
                            val original = updatedMessages[index] as GenAiTextMessage
                            updatedMessages[index] = original.copy(
                                loading = false,
                                response = original.response ?: "Couldn't generate a response"
                            )
                            emit(Either.Right(conversation.copy(messageList = updatedMessages)))
                        }
                    }
                    emit(Either.Left(mapHttpCodeToFailure(chatResponse.status.value)))
                }
            }

            analyticsLogger.logEvent(eventName = "Query Submitted", params = mapOf())
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            currentConversation?.let { conversation ->
                val updatedMessages = ArrayList(conversation.messageList)
                val index = updatedMessages.indexOfFirst { it.id == newMessageId }
                if (index != -1) {
                    val original = updatedMessages[index] as GenAiTextMessage
                    updatedMessages[index] = original.copy(
                        loading = false,
                        response = original.response ?: "Couldn't generate a response"
                    )
                    emit(Either.Right(conversation.copy(messageList = updatedMessages)))
                }
            }
            emit(Either.Left(Failure.ServerError))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun getConversation(conversation: ConversationPrompt): StructuredConversation {
        return if (conversationDao.getSingleConversation(conversation.conversationId) != null) {
            val existingConversation =
                conversationDao.getSingleConversation(conversation.conversationId)!!
            val messages = messageDao.getMessagesForConversation(conversation.conversationId)
                .map { it.toGenAiMessage() }.toMutableList()
            StructuredConversation(
                id = existingConversation.conversationId,
                title = existingConversation.title,
                messageList = messages
            )
        } else {
            // create new conversation
            val id = conversationDao.insertConversation(
                ConversationEntity(
                    title = "Default title",
                    createdAt = System.currentTimeMillis()
                )
            )

            StructuredConversation(
                id = id,
                title = "Default title",
                messageList = mutableListOf()
            )
        }
    }

    private suspend fun FlowCollector<Either<Failure, Conversation>>.handleSseStream(
        response: HttpResponse,
        initialConversation: StructuredConversation,
        prompt: String,
        messageId: Long
    ) {
        var currentConversation = initialConversation
        val chunk = StringBuilder()

        var currentEvent: String? = null

        try {
            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line()?.trim() ?: continue
                if (line.isEmpty()) {
                    currentEvent = null
                    continue
                }

                if (line.startsWith("event:")) {
                    currentEvent = line.substring(6).trim()
                } else if (line.startsWith("data:")) {
                    val data = line.substring(5).trim()
                    Timber.d("SSE Event: $currentEvent, Data: $data")
                    when (currentEvent) {
                        "text" -> {
                            try {
                                val textResponse = json.decodeFromString<TextStreamResponse>(data)
                                textResponse.text.let { text ->
                                    chunk.append(text)
                                    val updatedMessages = ArrayList(currentConversation.messageList)
                                    val index =
                                        updatedMessages.indexOfFirst { it.id == messageId && it is GenAiTextMessage }
                                    if (index != -1) {
                                        val original = updatedMessages[index] as GenAiTextMessage
                                        updatedMessages[index] =
                                            original.copy(
                                                response = chunk.toString(),
                                                loading = true // Keep loading true during stream
                                            )
                                        currentConversation =
                                            currentConversation.copy(messageList = updatedMessages)
                                        emit(Either.Right(currentConversation))
                                    }
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error parsing text event")
                            }
                        }

                        "conversation_id" -> {
                            try {
                                val conversationIdResponse = json.decodeFromString<ConversationIdResponse>(data)
                                conversationIdResponse.id.let { remoteId ->
                                    Timber.d("Syncing Remote ID: $remoteId")
                                    conversationDao.getSingleConversation(currentConversation.id)
                                        ?.let { entity ->
                                            conversationDao.updateConversation(entity.copy(remoteId = remoteId))
                                        }
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error parsing conversation_id event")
                            }
                        }

                        "entity" -> {
                            try {
                                val entity = json.decodeFromString<CompanyDetailRemoteResponse>(data)
                                if (currentConversation.messageList.any { it is GenAiEntityMessage && it.entity?.ticker == entity.ticker }) {
                                    // continue
                                } else {
                                    val updatedMessages = ArrayList(currentConversation.messageList)
                                    val newId = messageDao.insertMessage(
                                        MessageEntity(
                                            conversationId = currentConversation.id,
                                            companyDetailRemoteResponse = entity,
                                            createdAt = System.currentTimeMillis()
                                        )
                                    )
                                    // Try to insert before the current text message
                                    val textMsgIndex =
                                        updatedMessages.indexOfFirst { it.id == messageId }
                                    if (textMsgIndex != -1) {
                                        updatedMessages.add(
                                            textMsgIndex,
                                            GenAiEntityMessage(id = newId, entity = entity)
                                        )
                                    } else {
                                        updatedMessages.add(
                                            GenAiEntityMessage(
                                                id = newId,
                                                entity = entity
                                            )
                                        )
                                    }
                                    currentConversation =
                                        currentConversation.copy(messageList = updatedMessages)
                                    emit(Either.Right(currentConversation))
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error parsing entity event")
                            }
                        }

                        "suggestions" -> {
                            try {
                                val suggestionsResponse = json.decodeFromString<SuggestionResponse>(data)
                                suggestionsResponse.suggestions.let { suggestions ->
                                    val domainSuggestions = suggestions.map {
                                        SuggestionRemote(label = it.label ?: "", query = it.query ?: "")
                                    }
                                    currentConversation =
                                        currentConversation.copy(suggestedPrompts = domainSuggestions)
                                    emit(Either.Right(currentConversation))
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error parsing suggestions event")
                            }
                        }

                        "title" -> {
                            try {
                                val titleResponse = json.decodeFromString<ConversationTitleResponse>(data)
                                titleResponse.title.let { title ->
                                    currentConversation = currentConversation.copy(title = title)
                                    emit(Either.Right(currentConversation))
                                    conversationDao.getSingleConversation(currentConversation.id)?.let {
                                        conversationDao.updateConversation(it.copy(title = title))
                                    }
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error parsing title event")
                            }
                        }

                        "error" -> {
                            try {
                                val errorResponse = json.decodeFromString<ErrorResponse>(data)
                                errorResponse.error.let { error ->
                                    Timber.e("SSE Error: $error")
                                    val updatedMessages = ArrayList(currentConversation.messageList)
                                    val index =
                                        updatedMessages.indexOfFirst { it.id == messageId && it is GenAiTextMessage }
                                    if (index != -1) {
                                        val original = updatedMessages[index] as GenAiTextMessage
                                        updatedMessages[index] =
                                            original.copy(
                                                loading = false,
                                                response = original.response ?: "Couldn't generate a response"
                                            )
                                        currentConversation =
                                            currentConversation.copy(messageList = updatedMessages)
                                        emit(Either.Right(currentConversation))
                                    }
                                    emit(Either.Left(mapSseErrorToFailure(error)))
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error parsing error event")
                            }
                        }

                        "completion" -> {
                            Timber.d("SSE Stream Completed")
                            val finalResponseText = chunk.toString()
                            val updatedMessageEntity = messageDao.getSingleMessage(messageId)
                                .copy(response = finalResponseText)
                            messageDao.updateMessage(updatedMessageEntity)

                            // Final UI update to stop loading
                            val updatedMessages = ArrayList(currentConversation.messageList)
                            val index =
                                updatedMessages.indexOfFirst { it.id == messageId && it is GenAiTextMessage }
                            if (index != -1) {
                                val original = updatedMessages[index] as GenAiTextMessage
                                updatedMessages[index] =
                                    original.copy(response = finalResponseText, loading = false)
                                currentConversation =
                                    currentConversation.copy(messageList = updatedMessages)
                                emit(Either.Right(currentConversation))
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during SSE stream")
            val updatedMessages = ArrayList(currentConversation.messageList)
            val index =
                updatedMessages.indexOfFirst { it.id == messageId && it is GenAiTextMessage }
            if (index != -1) {
                val original = updatedMessages[index] as GenAiTextMessage
                updatedMessages[index] =
                    original.copy(
                        loading = false,
                        response = original.response ?: "Couldn't generate a response"
                    )
                currentConversation =
                    currentConversation.copy(messageList = updatedMessages)
                emit(Either.Right(currentConversation))
            }
            emit(Either.Left(Failure.ServerError))
        }
    }


    private fun mapHttpCodeToFailure(code: Int): Failure {
        return when (code) {
            429 -> Failure.RateLimitExceeded
            413 -> Failure.ContextLimitReached
            else -> Failure.ServerError
        }
    }

    private fun mapSseErrorToFailure(error: String): Failure {
        val normalized = error.lowercase()
        return when {
            normalized.contains("429") || normalized.contains("rate_limit") -> Failure.RateLimitExceeded
            normalized.contains("413") || normalized.contains("context_limit") -> Failure.ContextLimitReached
            else -> Failure.ServerError
        }
    }
}
