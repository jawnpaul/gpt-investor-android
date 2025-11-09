package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.thejawnpaul.gptinvestor.BuildConfig
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
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
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPromptParser
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private val apiService: ApiService,
    private val analyticsLogger: AnalyticsLogger,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val remoteConfig: RemoteConfig,
    private val gptInvestorPreferences: GPTInvestorPreferences,
    private val auth: FirebaseAuth
) :
    IConversationRepository {

    private val newModel = "gemini-1.5-pro-latest"
    private val oldModel = "gemini-1.0-pro"
    private val flashModel = "gemini-2.0-flash"

    private val rateLimitMutex = Mutex()

    private val generativeModel = GenerativeModel(
        modelName = remoteConfig.fetchAndActivateStringValue(Constants.MODEL_NAME_KEY),
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
        if (isRateLimitExceeded()) {
            emit(Either.Left(Failure.RateLimitExceeded))
            return@flow
        }

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
                    GenAiTextMessage(query = prompt.query, loading = true, feedbackStatus = 0)
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
        if (isRateLimitExceeded()) {
            emit(Either.Left(Failure.RateLimitExceeded))
            return@flow
        }

        try {
            // Use 'var' to allow reassignment of the conversation object
            var currentConversation = getConversation(prompt)
            Timber.e(currentConversation.id.toString())

            // TODO: Emit default loading state from here

            // check if input string contains entity
            val entityList = containsEntity(prompt.query)
            if (entityList.isNotEmpty()) {
                val ticker = entityList.first()
                val company = getCompanyDetail(ticker)

                // Ensure we are modifying a mutable list or creating new lists when copying
                val newMessages = ArrayList(currentConversation.messageList)

                if (newMessages.isEmpty()) {
                    company?.let {
                        val newId = messageDao.insertMessage(
                            MessageEntity(
                                conversationId = currentConversation.id,
                                companyDetailRemoteResponse = company,
                                createdAt = System.currentTimeMillis()
                            )
                        )
                        newMessages.add(
                            GenAiEntityMessage(
                                id = newId,
                                entity = company
                            )
                        )
                        currentConversation =
                            currentConversation.copy(messageList = newMessages)
                        emit(Either.Right(currentConversation))
                    }
                } else {
                    val existingEntity =
                        newMessages.filterIsInstance<GenAiEntityMessage>()
                            .find { it.entity?.ticker == ticker }
                    if (existingEntity == null) {
                        company?.let {
                            val newId = messageDao.insertMessage(
                                MessageEntity(
                                    conversationId = currentConversation.id,
                                    companyDetailRemoteResponse = company,
                                    createdAt = System.currentTimeMillis()
                                )
                            )
                            newMessages.add(
                                GenAiEntityMessage(
                                    id = newId,
                                    entity = company
                                )
                            )
                            currentConversation =
                                currentConversation.copy(messageList = newMessages)
                            emit(Either.Right(currentConversation))

                            analyticsLogger.logEvent(
                                eventName = "Company Identified",
                                params = mapOf("company_ticker" to company.ticker)
                            )
                        }
                    }
                }
            }

            val chunk = StringBuilder()

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

            val chat = generativeModel.startChat(history = getHistory(currentConversation.id))

            val response = chat.sendMessageStream(prompt.query)
            response.onCompletion {
                val suggested = getSuggestedPrompts(currentConversation.id)

                currentConversation = currentConversation.copy(suggestedPrompts = suggested)

                emit(Either.Right(currentConversation))

                Timber.e("Completed")

                // update message with complete response in DB
                // It's safer to find the message by ID and update its response
                // as the 'last' message might not be the one we intend to update if other messages were added.
                // However, given the current flow, messageList.last() should be the GenAiTextMessage.
                val finalResponseText = currentConversation.messageList
                    .filterIsInstance<GenAiTextMessage>()
                    .find { it.id == newMessageId }?.response
                    ?: chunk.toString() // Fallback to chunk if not found or not updated in currentConversation

                val updatedMessageEntity = messageDao.getSingleMessage(newMessageId)
                    .copy(response = finalResponseText) // Use the response from the conversation state
                messageDao.updateMessage(updatedMessageEntity)

                getConversationTitle(currentConversation.id)?.let { title ->
                    // NOW, currentConversation already has suggestedPrompts.
                    // So, copying it and adding the title will preserve them.
                    currentConversation = currentConversation.copy(title = title)
                    emit(Either.Right(currentConversation))
                    val entity = conversationDao.getSingleConversation(currentConversation.id)
                    entity?.let {
                        // Ensure the DAO is updated with both the new title and existing suggested prompts
                        // if your DAO's updateConversation can take the whole Conversation object or specific fields.
                        // If 'it' (entity from DAO) doesn't have suggested prompts,
                        // you might need to merge: it.copy(title = title, suggestedPrompts = currentConversation.suggestedPrompts)
                        // However, it's generally better to update the DAO with the 'currentConversation' state if possible.
                        conversationDao.updateConversation(it.copy(title = title)) // This only updates the title in DAO.
                        // Consider if suggestedPrompts also need to be persisted here.
                        // A safer bet if your DAO takes the whole object or specific fields:
                        // conversationDao.updateConversation(currentConversation.toEntity())
                        // or if you want to be explicit:
                        // conversationDao.updateConversation(it.copy(title = title, suggestedPrompts = currentConversation.suggestedPrompts))
                    }
                }
            }.collect { result ->
                result.text?.let { responseText ->
                    chunk.append(responseText)

                    // Create a new list for modification to maintain immutability
                    val updatedMessagesDuringStream = ArrayList(currentConversation.messageList)
                    val lastIndex = updatedMessagesDuringStream.lastIndex

                    if (lastIndex >= 0) { // Ensure the list is not empty
                        val lastMessage = updatedMessagesDuringStream[lastIndex]

                        when (lastMessage) {
                            is GenAiTextMessage -> {
                                val updatedTextMessage = lastMessage.copy(
                                    // query = prompt.query, // Query is already set when message was added
                                    response = chunk.toString(),
                                    loading = false
                                )
                                updatedMessagesDuringStream[lastIndex] = updatedTextMessage
                                currentConversation =
                                    currentConversation.copy(messageList = updatedMessagesDuringStream)
                                emit(Either.Right(currentConversation))
                            }

                            is GenAiEntityMessage -> {
                                Timber.e("Last message is Gen AI entity message, not updating with streaming text.")
                                // If an entity message was the last one, and a text response is streaming,
                                // you might need to decide how to handle this.
                                // Potentially, the new GenAiTextMessage for the query (added before starting chat)
                                // should be the one updated.
                                // Let's refine this to ensure we update the correct message:

                                val messageToUpdateIndex =
                                    updatedMessagesDuringStream.indexOfFirst { it.id == newMessageId && it is GenAiTextMessage }
                                if (messageToUpdateIndex != -1) {
                                    val textMessageToUpdate =
                                        updatedMessagesDuringStream[messageToUpdateIndex] as GenAiTextMessage
                                    updatedMessagesDuringStream[messageToUpdateIndex] =
                                        textMessageToUpdate.copy(
                                            response = chunk.toString(),
                                            loading = false
                                        )
                                    currentConversation =
                                        currentConversation.copy(messageList = updatedMessagesDuringStream)
                                    emit(Either.Right(currentConversation))
                                }
                            }
                        }
                    }
                }
            }

            analyticsLogger.logEvent(eventName = "Query Submitted", params = mapOf())
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
        if (isRateLimitExceeded()) {
            emit(Either.Left(Failure.RateLimitExceeded))
            return@flow
        }

        try {
            val chunk = StringBuilder()

            // get conversation
            var conversation =
                getConversation(ConversationPrompt(prompt.conversationId, prompt.query))
            if (prompt.company != null) {
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

            val newIndex = messageDao.insertMessage(
                MessageEntity(
                    conversationId = conversation.id,
                    createdAt = System.currentTimeMillis(),
                    query = prompt.query
                )
            )

            val newMessages = ArrayList(conversation.messageList)
            newMessages.add(
                GenAiTextMessage(
                    id = newIndex,
                    query = prompt.query,
                    feedbackStatus = 0
                )
            )

            conversation =
                conversation.copy(messageList = newMessages)

            emit(Either.Right(conversation))
            Timber.e(conversation.toString())
            // add query
            val chat = generativeModel.startChat(history = getHistory(conversation.id))

            // add text response
            val lastIndex = conversation.messageList.lastIndex

            val response = chat.sendMessageStream(prompt.query)
            response.onCompletion {
                val suggested = getSuggestedPrompts(conversation.id)

                conversation = conversation.copy(suggestedPrompts = suggested)

                emit(Either.Right(conversation))

                val updatedEntity = messageDao.getSingleMessage(newIndex)
                    .copy(response = conversation.messageList.last().response)
                messageDao.updateMessage(updatedEntity)

                getConversationTitle(conversation.id)?.let { title ->
                    conversation = conversation.copy(title = title)
                    emit(Either.Right(conversation))
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

    private suspend fun isRateLimitExceeded(): Boolean = rateLimitMutex.withLock {
        Timber.e("Checking rate limit")

        // Use UTC timezone to avoid timezone inconsistencies
        val todayString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())

        // Capture auth state at the beginning to avoid mid-function changes
        val isUserSignedIn = auth.currentUser != null

        try {
            val lastDate = gptInvestorPreferences.queryLastDate.first()
            val usageCount = gptInvestorPreferences.queryUsageCount.first() ?: 0

            val dailyLimit = try {
                if (isUserSignedIn) {
                    remoteConfig.fetchAndActivateValue(Constants.PROMPT_COUNT)
                        .toInt().takeIf { it > 0 } ?: 5
                } else {
                    remoteConfig.fetchAndActivateValue(Constants.FREE_PROMPT_COUNT)
                        .toInt().takeIf { it > 0 } ?: 2
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch remote config, using fallback values")
                if (isUserSignedIn) 5 else 2
            }

            // Determine usage count *before* this request. Reset if it's a new day.
            val isNewDay = todayString != lastDate
            val usageBeforeThisRequest = if (isNewDay) 0 else usageCount

            Timber.e("User signed in: $isUserSignedIn, Daily limit: $dailyLimit, Current usage: $usageBeforeThisRequest")

            // Check if limit has already been reached
            if (usageBeforeThisRequest >= dailyLimit) {
                Timber.e("Rate limit exceeded: $usageBeforeThisRequest >= $dailyLimit")
                analyticsLogger.logEvent(
                    eventName = "Rate Limit Reached",
                    params = mapOf(
                        "limit" to dailyLimit,
                        "current_count" to usageBeforeThisRequest,
                        "user_signed_in" to isUserSignedIn
                    )
                )
                return@withLock true
            }

            // If not exceeded, allow the request and update the count.
            Timber.e("Rate limit not exceeded. Incrementing count.")
            try {
                if (isNewDay) {
                    gptInvestorPreferences.setQueryLastDate(todayString)
                    Timber.e("New day, resetting date.")
                }
                gptInvestorPreferences.setQueryUsageCount(usageBeforeThisRequest + 1)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save updated usage count")
                // Still allow the request even if save fails
            }
            return@withLock false
        } catch (e: Exception) {
            Timber.e(e, "Error checking rate limit, allowing request as fallback")
            // In case of unexpected errors, allow the request rather than blocking user
            return@withLock false
        }
    }
}
