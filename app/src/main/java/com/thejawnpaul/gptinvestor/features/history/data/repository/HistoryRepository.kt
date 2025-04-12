package com.thejawnpaul.gptinvestor.features.history.data.repository

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.history.domain.repository.IHistoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class HistoryRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val analyticsLogger: AnalyticsLogger
) :
    IHistoryRepository {
    override suspend fun getAllHistory(): Flow<Either<Failure, List<StructuredConversation>>> = flow {
        try {
            val conversations = conversationDao.getConversationsWithMessages().map { entity ->
                with(entity) {
                    StructuredConversation(
                        id = conversation.conversationId,
                        title = conversation.title
                    )
                }
            }
            emit(Either.Right(conversations))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun getSingleHistory(id: Long): Flow<Either<Failure, StructuredConversation>> = flow {
        try {
            val conversation = with(conversationDao.getSingleConversationWithMessages(id)) {
                StructuredConversation(
                    id = conversation.conversationId,
                    title = conversation.title,
                    messageList = messages.map { it.toGenAiMessage() }.toMutableList()
                )
            }
            emit(Either.Right(conversation))
            analyticsLogger.logEvent(
                eventName = "History Selected",
                params = mapOf("chat_title" to conversation.title)
            )
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }
}
