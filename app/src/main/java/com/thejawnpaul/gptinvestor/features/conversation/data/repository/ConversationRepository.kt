package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
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
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.GetEntityRequest
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private val apiService: ApiService,
    private val analyticsLogger: AnalyticsLogger,
    private val companyDao: CompanyDao
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

    override suspend fun getDefaultPromptResponse(prompt: DefaultPrompt): Flow<Either<Failure, Conversation>> =
        flow {
            try {
                val chunk = StringBuilder()

                analyticsLogger.logDefaultPromptSelected(
                    promptTitle = prompt.title,
                    promptQuery = prompt.query
                )

                val structuredConversation = StructuredConversation(
                    id = conversationId,
                    title = prompt.title,
                    messageList = mutableListOf(
                        GenAiTextMessage(query = prompt.query, loading = true)
                    )
                )
                emit(Either.Right(structuredConversation))

                val chat = generativeModel.startChat(history = getHistory(structuredConversation))

                val response = chat.sendMessageStream(prompt.query)

                response.collect { result ->
                    result.text?.let { responseText ->

                        chunk.append(responseText)

                        val lastIndex = structuredConversation.messageList.lastIndex
                        val message =
                            structuredConversation.messageList[lastIndex] as GenAiTextMessage
                        val last = message.copy(
                            query = prompt.query,
                            response = chunk.toString(),
                            loading = false
                        )
                        structuredConversation.messageList[lastIndex] = last

                        emit(Either.Right(structuredConversation))

                        database[conversationId] = structuredConversation
                    }
                }
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

    override suspend fun getInputResponse(prompt: ConversationPrompt): Flow<Either<Failure, Conversation>> =
        flow {
            try {
                val conversation = getConversation(prompt)

                // check if input string contains entity
                val entityList = containsEntity(prompt.query)
                if (entityList.isNotEmpty()) {
                    val ticker = entityList.first()
                    val company = getCompanyDetail(ticker)

                    if (conversation.messageList.isEmpty()) {

                        company?.let {
                            // emit company
                            val newId = 0L
                            conversation.messageList.add(
                                GenAiEntityMessage(
                                    id = newId,
                                    entity = company
                                )
                            )

                            emit(Either.Right(conversation))
                            // add company to conversation history
                        }
                    } else {
                        // check if this returned entity doesn't exist in the conversation, add it
                        val existingEntity =
                            conversation.messageList.filterIsInstance<GenAiEntityMessage>()
                                .find { it.entity?.ticker == ticker }
                        if (existingEntity == null) {
                            //add it to the conversation
                            company?.let {
                                // emit company
                                val newId = conversation.messageList.last().id + 1
                                conversation.messageList.add(
                                    GenAiEntityMessage(
                                        id = newId,
                                        entity = company
                                    )
                                )

                                emit(Either.Right(conversation))
                                // add company to conversation history
                            }
                        } else {
                            // do nothing or update it
                        }

                    }

                }

                // if there's an existing conversation, append new message else create a new conversation
                if (database.containsKey(prompt.conversationId)) {
                    Timber.e("Existing conversation")

                    val chunk = StringBuilder()

                    // get the previous messages in the conversation, add as context

                    val newMessageId =
                        if (conversation.messageList.isNotEmpty()) conversation.messageList.last().id + 1 else 0
                    val message =
                        GenAiTextMessage(id = newMessageId, query = prompt.query, loading = true)

                    conversation.messageList.add(message)

                    emit(Either.Right(conversation))

                    Timber.e(conversation.toString())

                    val chat = generativeModel.startChat(history = getHistory(conversation))

                    val response = chat.sendMessageStream(prompt.query)
                    response.collect { result ->
                        result.text?.let { responseText ->

                            chunk.append(responseText)

                            val lastIndex = conversation.messageList.lastIndex
                            val lastMessage =
                                conversation.messageList[lastIndex]

                            when (lastMessage) {
                                is GenAiTextMessage -> {
                                    val last = lastMessage.copy(
                                        query = prompt.query,
                                        response = chunk.toString(),
                                        loading = false
                                    )
                                    conversation.messageList[lastIndex] = last

                                    emit(Either.Right(conversation))

                                    database[conversationId] = conversation
                                }

                                is GenAiEntityMessage -> {
                                    Timber.e("Last message is Gen AI entity message")
                                }
                            }
                        }
                    }
                } else {
                    Timber.e("No existing conversation")
                    val chunk = StringBuilder()

                    val newMessageId =
                        if (conversation.messageList.isNotEmpty()) conversation.messageList.last().id + 1 else 0

                    conversation.messageList.add(
                        GenAiTextMessage(
                            id = newMessageId,
                            query = prompt.query,
                            loading = true
                        )
                    )

                    emit(Either.Right(conversation))

                    Timber.e(conversation.toString())

                    val chat = generativeModel.startChat(history = getHistory(conversation))

                    val response = chat.sendMessageStream(prompt.query)
                    response.collect { result ->
                        result.text?.let { responseText ->

                            chunk.append(responseText)

                            val lastIndex = conversation.messageList.lastIndex
                            val lastMessage =
                                conversation.messageList[lastIndex]

                            when (lastMessage) {
                                is GenAiTextMessage -> {
                                    val last = lastMessage.copy(
                                        query = prompt.query,
                                        response = chunk.toString(),
                                        loading = false
                                    )
                                    conversation.messageList[lastIndex] = last

                                    emit(Either.Right(conversation))

                                    database[conversationId] = conversation
                                }

                                is GenAiEntityMessage -> {
                                    Timber.e("Last message is Gen AI entity message")
                                }
                            }
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

    override suspend fun getCompanyInputResponse(prompt: CompanyPrompt): Flow<Either<Failure, Conversation>> =
        flow {
            try {
                val chunk = StringBuilder()
                Timber.e(prompt.toString())

                //get conversation
                val conversation = getConversation(ConversationPrompt(0, prompt.query))
                if (prompt.company != null) {
                    conversation.messageList.add(
                        GenAiEntityMessage(id = 1, entity = prompt.company)
                    )
                }

                val newIndex =
                    if (conversation.messageList.isNotEmpty()) conversation.messageList.last().id + 1 else 1

                conversation.messageList.add(GenAiTextMessage(id = newIndex, prompt.query))

                emit(Either.Right(conversation))
                Timber.e(conversation.toString())
                // add query
                val chat = generativeModel.startChat(history = getHistory(conversation))

                //add text response
                val lastIndex = conversation.messageList.lastIndex

                val response = chat.sendMessageStream(prompt.query)
                response.collect { result ->
                    result.text?.let { responseText ->
                        chunk.append(responseText)

                        val lastMessage =
                            conversation.messageList[lastIndex]

                        when (lastMessage) {
                            is GenAiTextMessage -> {
                                val last = lastMessage.copy(
                                    query = prompt.query,
                                    response = chunk.toString(),
                                    loading = false
                                )
                                conversation.messageList[lastIndex] = last

                                emit(Either.Right(conversation))

                                database[conversationId] = conversation
                            }

                            is GenAiEntityMessage -> {
                                Timber.e("Last message is Gen AI entity message")
                            }
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

    private suspend fun containsEntity(input: String): List<String> {
        return apiService.getEntity(GetEntityRequest(query = input)).body()?.entityList
            ?: emptyList()
    }

    private fun getConversation(conversation: ConversationPrompt): StructuredConversation {
        return if (database.containsKey(conversation.conversationId)) {
            database[conversation.conversationId]!! as StructuredConversation
        } else {
            StructuredConversation(
                id = conversationId,
                title = "Default title",
                messageList = mutableListOf()
            )
        }
    }

    private fun getHistory(conversation: StructuredConversation): List<Content> {
        val history = mutableListOf<Content>()
        conversation.messageList.forEach { message ->
            when (message) {
                is GenAiTextMessage -> {
                    history.addAll(
                        listOf(
                            content(role = "user") { text(message.query) },
                            content(role = "model") { text(message.response.toString()) }
                        )
                    )
                }

                is GenAiEntityMessage -> {
                    history.add(content(role = "user") { text(message.entity.toString()) })
                }
            }
        }
        return history
    }

    private suspend fun getCompanyDetail(ticker: String): CompanyDetailRemoteResponse? {
        return apiService.getCompanyInfo(CompanyDetailRemoteRequest(ticker = ticker)).body()
    }
}
