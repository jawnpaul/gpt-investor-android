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
import com.thejawnpaul.gptinvestor.analytics.Analytics
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.GetEntityRequest
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import timber.log.Timber

class ConversationRepository @Inject constructor(
    private val apiService: ApiService,
    private val analyticsLogger: Analytics,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao
) :
    IConversationRepository {

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
            val chunk = StringBuilder()

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

            val structuredConversation = StructuredConversation(
                id = conversationId,
                title = prompt.title,
                messageList = mutableListOf(
                    GenAiTextMessage(query = prompt.query, loading = true)
                )
            )
            emit(Either.Right(structuredConversation))

            val chat =
                generativeModel.startChat(history = getHistory(structuredConversation.id))

            val response = chat.sendMessageStream(prompt.query)

            response.onCompletion {
                val suggested = getSuggestedPrompts(conversationId)
                emit(Either.Right(structuredConversation.copy(suggestedPrompts = suggested)))

                // insert new message entity
                val message = MessageEntity(
                    conversationId = conversationId,
                    query = prompt.query,
                    response = structuredConversation.messageList.last().response,
                    createdAt = System.currentTimeMillis()
                )
                messageDao.insertMessage(message)
            }.collect { result ->
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

    override suspend fun getInputResponse(prompt: ConversationPrompt): Flow<Either<Failure, Conversation>> = flow {
        try {
            val conversation = getConversation(prompt)
            Timber.e(conversation.id.toString())

            // TODO: Emit default loading state from here

            // check if input string contains entity
            val entityList = containsEntity(prompt.query)
            if (entityList.isNotEmpty()) {
                val ticker = entityList.first()
                val company = getCompanyDetail(ticker)

                if (conversation.messageList.isEmpty()) {
                    company?.let {
                        // emit company
                        val newId = messageDao.insertMessage(
                            MessageEntity(
                                conversationId = conversation.id,
                                companyDetailRemoteResponse = company,
                                createdAt = System.currentTimeMillis()
                            )
                        )
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
                        // add it to the conversation
                        company?.let {
                            // emit company
                            val newId = messageDao.insertMessage(
                                MessageEntity(
                                    conversationId = conversation.id,
                                    companyDetailRemoteResponse = company,
                                    createdAt = System.currentTimeMillis()
                                )
                            )
                            conversation.messageList.add(
                                GenAiEntityMessage(
                                    id = newId,
                                    entity = company
                                )
                            )

                            emit(Either.Right(conversation))
                            // add company to conversation history

                            analyticsLogger.logEvent(
                                eventName = "Company Identified",
                                params = mapOf("company_ticker" to company.ticker)
                            )
                        }
                    } else {
                        // do nothing or update it
                    }
                }
            }

            val chunk = StringBuilder()

            val newMessageId =
                messageDao.insertMessage(
                    MessageEntity(
                        conversationId = conversation.id,
                        createdAt = System.currentTimeMillis(),
                        query = prompt.query
                    )
                )

            conversation.messageList.add(
                GenAiTextMessage(
                    id = newMessageId,
                    query = prompt.query,
                    loading = true
                )
            )

            emit(Either.Right(conversation))

            val chat = generativeModel.startChat(history = getHistory(conversation.id))

            val response = chat.sendMessageStream(prompt.query)
            response.onCompletion {
                val suggested = getSuggestedPrompts(conversation.id)
                emit(Either.Right(conversation.copy(suggestedPrompts = suggested)))
                Timber.e("Completed")

                // update message with complete response
                val updatedEntity = messageDao.getSingleMessage(newMessageId)
                    .copy(response = conversation.messageList.last().response)
                messageDao.updateMessage(updatedEntity)

                getConversationTitle(conversation.id)?.let { title ->
                    emit(Either.Right(conversation.copy(title = title)))
                    val entity = conversationDao.getSingleConversation(conversation.id)
                    entity?.let {
                        conversationDao.updateConversation(it.copy(title = title))
                    }
                }
            }.collect { result ->
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

    override suspend fun getCompanyInputResponse(prompt: CompanyPrompt): Flow<Either<Failure, Conversation>> = flow {
        try {
            val chunk = StringBuilder()

            // get conversation
            val conversation =
                getConversation(ConversationPrompt(prompt.conversationId, prompt.query))
            if (prompt.company != null) {
                val id = messageDao.insertMessage(
                    MessageEntity(
                        conversationId = conversation.id,
                        createdAt = System.currentTimeMillis(),
                        companyDetailRemoteResponse = prompt.company
                    )
                )
                conversation.messageList.add(
                    GenAiEntityMessage(id = id, entity = prompt.company)
                )
            }

            val newIndex = messageDao.insertMessage(
                MessageEntity(
                    conversationId = conversation.id,
                    createdAt = System.currentTimeMillis(),
                    query = prompt.query
                )
            )

            conversation.messageList.add(GenAiTextMessage(id = newIndex, prompt.query))

            emit(Either.Right(conversation))
            Timber.e(conversation.toString())
            // add query
            val chat = generativeModel.startChat(history = getHistory(conversation.id))

            // add text response
            val lastIndex = conversation.messageList.lastIndex

            val response = chat.sendMessageStream(prompt.query)
            response.onCompletion {
                val suggested = getSuggestedPrompts(conversation.id)
                emit(Either.Right(conversation.copy(suggestedPrompts = suggested)))

                val updatedEntity = messageDao.getSingleMessage(newIndex)
                    .copy(response = conversation.messageList.last().response)
                messageDao.updateMessage(updatedEntity)

                getConversationTitle(conversation.id)?.let { title ->
                    emit(Either.Right(conversation.copy(title = title)))
                    val entity = conversationDao.getSingleConversation(conversation.id)
                    entity?.let {
                        conversationDao.updateConversation(it.copy(title = title))
                    }
                }
            }.collect { result ->
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

    private suspend fun getSuggestedPrompts(conversationId: Long): List<Suggestion> {
        return try {
            val result = mutableListOf<Suggestion>()
            val parser = SuggestionParser()

            val conversation =
                getConversation(ConversationPrompt(conversationId = conversationId, query = ""))
            val chat = generativeModel.startChat(history = getHistory(conversation.id))
            val response = chat.sendMessage(prompt = Constants.SUGGESTION_PROMPT)
            response.text?.let {
                val text = it.trimIndent().removeSurrounding("```").removePrefix("json")
                val suggestions = parser.parseSuggestions(text)
                suggestions?.let { suggestionsResponse ->
                    result.addAll(suggestionsResponse.suggestions)
                }
            }
            result
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emptyList()
        }
    }

    private suspend fun containsEntity(input: String): List<String> {
        return apiService.getEntity(GetEntityRequest(query = input)).body()?.entityList
            ?: emptyList()
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

    private suspend fun getHistory(conversationId: Long): List<Content> {
        val history = mutableListOf<Content>()
        val messageEntities = messageDao.getMessagesForConversation(conversationId)
        messageEntities.map { it.toGenAiMessage() }.forEach { message ->
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

    private suspend fun getConversationTitle(conversationId: Long): String? {
        return try {
            val parser = ConversationTitleParser()
            val conversationEntity = conversationDao.getSingleConversation(conversationId)
            conversationEntity?.let { conversation ->
                if (conversation.title == "Default title" || conversation.title.isEmpty()) {
                    val history = getHistory(conversationId)

                    val chat = generativeModel.startChat(history = history)
                    val response = chat.sendMessage(prompt = Constants.TITLE_PROMPT)
                    response.text?.let {
                        val text = it.trimIndent().removeSurrounding("```").removePrefix("json")
                        parser.parseTitle(text)?.title
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            null
        }
    }
}
