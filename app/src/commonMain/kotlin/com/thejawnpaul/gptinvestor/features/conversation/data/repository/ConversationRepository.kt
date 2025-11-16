package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.firebase.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
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
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.HistoryContent
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IFirebaseAiApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ConversationRepository(
    private val apiService: ApiService,
    private val analyticsLogger: AnalyticsLogger,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val remoteConfig: RemoteConfig,
    private val firebaseAiApi: IFirebaseAiApi,
    private val gptInvestorPreferences: GPTInvestorPreferences,
) : IConversationRepository {

    private val rateLimitMutex = Mutex()

    override suspend fun getDefaultPrompts(): Flow<Either<Failure, List<DefaultPrompt>>> = flow {
        try {
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
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getDefaultPromptResponse(prompt: DefaultPrompt): Flow<Either<Failure, Conversation>> =
        flow {
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
                        createdAt = Clock.System.now().toEpochMilliseconds()
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

                val response = firebaseAiApi.sendMessageStream(
                    history = getHistory(structuredConversation.id),
                    prompt = prompt.query
                )

                response.onCompletion {
                    val suggested = getSuggestedPrompts(conversationId)
                    emit(Either.Right(structuredConversation.copy(suggestedPrompts = suggested)))

                    // insert new message entity
                    val message = MessageEntity(
                        conversationId = conversationId,
                        query = prompt.query,
                        response = structuredConversation.messageList.last().response,
                        createdAt = Clock.System.now().toEpochMilliseconds()
                    )
                    messageDao.insertMessage(message)
                }.collect { result ->
                    result?.let { responseText ->

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
                    is RuntimeException -> {
                        emit(Either.Left(GenAIException()))
                        Logger.e(e.stackTraceToString())
                    }

                    else -> {
                        Logger.e(e.stackTraceToString())
                        emit(Either.Left(Failure.ServerError))
                    }
                }
            }
        }

    override suspend fun getInputResponse(prompt: ConversationPrompt): Flow<Either<Failure, Conversation>> =
        flow {
            if (isRateLimitExceeded()) {
                emit(Either.Left(Failure.RateLimitExceeded))
                return@flow
            }

            try {
                // Use 'var' to allow reassignment of the conversation object
                var currentConversation = getConversation(prompt)
                Logger.e(currentConversation.id.toString())

                // TODO: Emit default loading state from here

                // check if input string contains entity
                val entityList = containsEntity(prompt.query)
                if (entityList.isNotEmpty()) {
                    val ticker = entityList.first()
                    val company = getCompanyDetail(ticker)

                    // Ensure we are modifying a mutable list or creating new lists when copying
                    val newMessages = ArrayList(currentConversation.messageList)

                    if (newMessages.isEmpty()) {
                        company.let {
                            val newId = messageDao.insertMessage(
                                MessageEntity(
                                    conversationId = currentConversation.id,
                                    companyDetailRemoteResponse = company,
                                    createdAt = Clock.System.now().toEpochMilliseconds()
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
                            company.let {
                                val newId = messageDao.insertMessage(
                                    MessageEntity(
                                        conversationId = currentConversation.id,
                                        companyDetailRemoteResponse = company,
                                        createdAt = Clock.System.now().toEpochMilliseconds()
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
                            createdAt = Clock.System.now().toEpochMilliseconds(),
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

                val response = firebaseAiApi.sendMessageStream(
                    history = getHistory(currentConversation.id),
                    prompt = prompt.query
                )
                response.onCompletion {
                    val suggested = getSuggestedPrompts(currentConversation.id)

                    currentConversation = currentConversation.copy(suggestedPrompts = suggested)

                    emit(Either.Right(currentConversation))

                    Logger.e("Completed")

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
                    result?.let { responseText ->
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
                                    Logger.e("Last message is Gen AI entity message, not updating with streaming text.")
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
                    is RuntimeException -> {
                        Logger.e(e.stackTraceToString())
                        emit(Either.Left(GenAIException()))
                    }

                    else -> {
                        Logger.e(e.stackTraceToString())
                        emit(Either.Left(Failure.ServerError))
                    }
                }
            }
        }

    override suspend fun getCompanyInputResponse(prompt: CompanyPrompt): Flow<Either<Failure, Conversation>> =
        flow {
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
                            createdAt = Clock.System.now().toEpochMilliseconds(),
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
                        createdAt = Clock.System.now().toEpochMilliseconds(),
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
                Logger.e(conversation.toString())

                // add text response
                val lastIndex = conversation.messageList.lastIndex

                val response =
                    firebaseAiApi.sendMessageStream(
                        history = getHistory(conversation.id),
                        prompt = prompt.query
                    )
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
                    result?.let { responseText ->
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
                                Logger.e("Last message is Gen AI entity message")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is RuntimeException -> {
                        Logger.e(e.stackTraceToString())
                        emit(Either.Left(GenAIException()))
                    }

                    else -> {
                        Logger.e(e.stackTraceToString())
                        emit(Either.Left(Failure.ServerError))
                    }
                }
            }
        }

    private suspend fun getSuggestedPrompts(conversationId: Long): List<Suggestion> = try {
        val result = mutableListOf<Suggestion>()
        val parser = SuggestionParser()

        val conversation =
            getConversation(ConversationPrompt(conversationId = conversationId, query = ""))
        val response = firebaseAiApi.sendMessage(
            history = getHistory(conversation.id),
            prompt = Constants.SUGGESTION_PROMPT
        )
        response?.let {
            val text = it.trimIndent().removeSurrounding("```").removePrefix("json")
            val suggestions = parser.parseSuggestions(text)
            suggestions?.let { suggestionsResponse ->
                result.addAll(suggestionsResponse.suggestions)
            }
        }
        result
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        emptyList()
    }

    private suspend fun containsEntity(input: String): List<String> =
        apiService.getEntity(GetEntityRequest(query = input)).entityList

    private suspend fun getConversation(conversation: ConversationPrompt): StructuredConversation =
        if (conversationDao.getSingleConversation(conversation.conversationId) != null) {
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
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
            )

            StructuredConversation(
                id = id,
                title = "Default title",
                messageList = mutableListOf()
            )
        }

    private suspend fun getHistory(conversationId: Long): List<HistoryContent> {
        val history = mutableListOf<HistoryContent>()
        val messageEntities = messageDao.getMessagesForConversation(conversationId)
        messageEntities.map { it.toGenAiMessage() }.forEach { message ->
            when (message) {
                is GenAiTextMessage -> {
                    history.addAll(
                        listOf(
                            HistoryContent(role = "user", message =  message.query),
                            HistoryContent(role = "model", message = message.response.toString())
                        )
                    )
                }

                is GenAiEntityMessage -> {
                    history.add(HistoryContent(role = "user", message = message.entity.toString()))
                }
            }
        }
        return history
    }

    private suspend fun getCompanyDetail(ticker: String): CompanyDetailRemoteResponse =
        apiService.getCompanyInfo(CompanyDetailRemoteRequest(ticker = ticker))

    private suspend fun getConversationTitle(conversationId: Long): String? = try {
        val parser = ConversationTitleParser()
        val conversationEntity = conversationDao.getSingleConversation(conversationId)
        conversationEntity?.let { conversation ->
            if (conversation.title == "Default title" || conversation.title.isEmpty()) {
                val history = getHistory(conversationId)

                val response =
                    firebaseAiApi.sendMessage(history = history, prompt = Constants.TITLE_PROMPT)
                response?.let {
                    val text = it.trimIndent().removeSurrounding("```").removePrefix("json")
                    parser.parseTitle(text)?.title
                }
            } else {
                null
            }
        }
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        null
    }

    private suspend fun isRateLimitExceeded(): Boolean = rateLimitMutex.withLock {
        Logger.e("Checking rate limit")

        // Use UTC timezone to avoid timezone inconsistencies
        val todayString = Clock.System.now()
            .toLocalDateTime(TimeZone.UTC)
            .date
            .format(
                LocalDate.Format {
                    year()
                    char('-')
                    monthNumber()
                    char('-')
                    day()
                }
            )

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
                Logger.e("Failed to fetch remote config, using fallback values", e)
                if (isUserSignedIn) 5 else 2
            }

            // Determine usage count *before* this request. Reset if it's a new day.
            val isNewDay = todayString != lastDate
            val usageBeforeThisRequest = if (isNewDay) 0 else usageCount

            Logger.e("User signed in: $isUserSignedIn, Daily limit: $dailyLimit, Current usage: $usageBeforeThisRequest")

            // Check if limit has already been reached
            if (usageBeforeThisRequest >= dailyLimit) {
                Logger.e("Rate limit exceeded: $usageBeforeThisRequest >= $dailyLimit")
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
            Logger.e("Rate limit not exceeded. Incrementing count.")
            try {
                if (isNewDay) {
                    gptInvestorPreferences.setQueryLastDate(todayString)
                    Logger.e("New day, resetting date.")
                }
                gptInvestorPreferences.setQueryUsageCount(usageBeforeThisRequest + 1)
            } catch (e: Exception) {
                Logger.e("Failed to save updated usage count", e)
                // Still allow the request even if save fails
            }
            return@withLock false
        } catch (e: Exception) {
            Logger.e( "Error checking rate limit, allowing request as fallback", e)
            // In case of unexpected errors, allow the request rather than blocking user
            return@withLock false
        }
    }
}
