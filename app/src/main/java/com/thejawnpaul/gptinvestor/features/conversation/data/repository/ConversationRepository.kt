package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.squareup.moshi.Moshi
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
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
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.TextStreamResponse
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPromptParser
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import timber.log.Timber

class ConversationRepository @Inject constructor(
    private val apiService: ApiService,
    private val analyticsLogger: AnalyticsLogger,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val remoteConfig: RemoteConfig,
    private val moshi: Moshi
) :
    IConversationRepository {

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

            val messageId = messageDao.insertMessage(
                MessageEntity(
                    conversationId = conversationId,
                    query = prompt.query,
                    createdAt = System.currentTimeMillis()
                )
            )

            val structuredConversation = StructuredConversation(
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
            val chatResponse = apiService.chatAiResponse(aiChatRequest)

            if (chatResponse.isSuccessful) {
                chatResponse.body()?.let { responseBody ->
                    handleSseStream(
                        responseBody = responseBody,
                        initialConversation = structuredConversation,
                        prompt = prompt.query,
                        messageId = messageId
                    )
                }
            } else {
                emit(Either.Left(mapHttpCodeToFailure(chatResponse.code())))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getInputResponse(prompt: ConversationPrompt): Flow<Either<Failure, Conversation>> = flow {
        try {
            // Use 'var' to allow reassignment of the conversation object
            var currentConversation = getConversation(prompt)
            Timber.e(currentConversation.id.toString())

            val newMessageId =
                messageDao.insertMessage(
                    MessageEntity(
                        conversationId = currentConversation.id,
                        createdAt = System.currentTimeMillis(),
                        query = prompt.query
                    )
                )

            // Ensure we are modifying a mutable list or creating new lists when copying
            val messagesWithNewQuery = ArrayList(currentConversation.messageList)
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
                conversationId = entity?.remoteId
            )
            val chatResponse = apiService.chatAiResponse(aiChatRequest)

            if (chatResponse.isSuccessful) {
                chatResponse.body()?.let { responseBody ->
                    handleSseStream(
                        responseBody,
                        currentConversation,
                        prompt.query,
                        newMessageId
                    )
                }
            } else {
                emit(Either.Left(mapHttpCodeToFailure(chatResponse.code())))
            }

            analyticsLogger.logEvent(eventName = "Query Submitted", params = mapOf())
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getCompanyInputResponse(prompt: CompanyPrompt): Flow<Either<Failure, Conversation>> = flow {
        try {
            // get conversation
            var conversation =
                getConversation(ConversationPrompt(prompt.conversationId, prompt.query))
            if (prompt.company != null) {
                val alreadyExists = conversation.messageList.any {
                    it is GenAiEntityMessage && it.entity?.ticker == prompt.company.ticker
                }
                if (!alreadyExists) {
                    val newMessages = ArrayList(conversation.messageList)

                    val newId = messageDao.insertMessage(
                        MessageEntity(
                            conversationId = conversation.id,
                            createdAt = System.currentTimeMillis(),
                            companyDetailRemoteResponse = prompt.company
                        )
                    )

                    newMessages.add(
                        GenAiEntityMessage(id = newId, entity = prompt.company)
                    )

                    conversation = conversation.copy(messageList = newMessages)
                    emit(Either.Right(conversation))
                }
            }

            val newMessageId = messageDao.insertMessage(
                MessageEntity(
                    conversationId = conversation.id,
                    createdAt = System.currentTimeMillis(),
                    query = prompt.query
                )
            )

            val newMessages = ArrayList(conversation.messageList)
            newMessages.add(
                GenAiTextMessage(
                    id = newMessageId,
                    query = prompt.query,
                    loading = true,
                    feedbackStatus = 0
                )
            )

            conversation =
                conversation.copy(messageList = newMessages)

            emit(Either.Right(conversation))

            val entity = conversationDao.getSingleConversation(conversation.id)
            val aiChatRequest = AiChatRequest(
                prompt = prompt.query,
                conversationId = entity?.remoteId
            )
            val chatResponse = apiService.chatAiResponse(aiChatRequest)

            if (chatResponse.isSuccessful) {
                chatResponse.body()?.let { responseBody ->
                    handleSseStream(responseBody, conversation, prompt.query, newMessageId)
                }
            } else {
                emit(Either.Left(mapHttpCodeToFailure(chatResponse.code())))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

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
        responseBody: ResponseBody,
        initialConversation: StructuredConversation,
        prompt: String,
        messageId: Long
    ) {
        var currentConversation = initialConversation
        val chunk = StringBuilder()

        val textAdapter = moshi.adapter(TextStreamResponse::class.java)
        val entityAdapter = moshi.adapter(CompanyDetailRemoteResponse::class.java)
        val suggestionsAdapter = moshi.adapter(SuggestionsResponse::class.java)
        val titleAdapter = moshi.adapter(ConversationTitleResponse::class.java)
        val errorAdapter = moshi.adapter(ErrorResponse::class.java)
        val conversationIdAdapter = moshi.adapter(ConversationIdResponse::class.java)

        var currentEvent: String? = null

        try {
            val source = responseBody.source()
            while (!source.exhausted()) {
                val line = source.readUtf8Line()?.trim() ?: continue
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
                            textAdapter.fromJson(data)?.text?.let { text ->
                                chunk.append(text)
                                val updatedMessages = ArrayList(currentConversation.messageList)
                                val index =
                                    updatedMessages.indexOfFirst { it.id == messageId && it is GenAiTextMessage }
                                if (index != -1) {
                                    val original = updatedMessages[index] as GenAiTextMessage
                                    updatedMessages[index] =
                                        original.copy(
                                            response = chunk.toString(),
                                            loading = false // Set loading false as soon as text arrives
                                        )
                                    currentConversation =
                                        currentConversation.copy(messageList = updatedMessages)
                                    emit(Either.Right(currentConversation))
                                }
                            }
                        }

                        "conversation_id" -> {
                            conversationIdAdapter.fromJson(data)?.id?.let { remoteId ->
                                Timber.d("Syncing Remote ID: $remoteId")
                                conversationDao.getSingleConversation(currentConversation.id)
                                    ?.let { entity ->
                                        conversationDao.updateConversation(entity.copy(remoteId = remoteId))
                                    }
                            }
                        }

                        "entity" -> {
                            entityAdapter.fromJson(data)?.let { entity ->
                                if (currentConversation.messageList.any { it is GenAiEntityMessage && it.entity?.ticker == entity.ticker }) {
                                    return@let
                                }
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
                        }

                        "suggestions" -> {
                            suggestionsAdapter.fromJson(data)?.suggestions?.let { suggestions ->
                                val domainSuggestions = suggestions.map {
                                    Suggestion(label = it.label, query = it.query)
                                }
                                currentConversation =
                                    currentConversation.copy(suggestedPrompts = domainSuggestions)
                                emit(Either.Right(currentConversation))
                            }
                        }

                        "title" -> {
                            titleAdapter.fromJson(data)?.title?.let { title ->
                                currentConversation = currentConversation.copy(title = title)
                                emit(Either.Right(currentConversation))
                                conversationDao.getSingleConversation(currentConversation.id)?.let {
                                    conversationDao.updateConversation(it.copy(title = title))
                                }
                            }
                        }

                        "error" -> {
                            errorAdapter.fromJson(data)?.error?.let { error ->
                                Timber.e("SSE Error: $error")
                                emit(Either.Left(mapSseErrorToFailure(error)))
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
            normalized.contains("429") || normalized.contains("rate limit") -> Failure.RateLimitExceeded
            normalized.contains("413") || normalized.contains("context limit") -> Failure.ContextLimitReached
            else -> Failure.ServerError
        }
    }
}
