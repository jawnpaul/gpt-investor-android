package com.thejawnpaul.gptinvestor.features.history.data.repository

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.utility.formatAsRelativeDate
import com.thejawnpaul.gptinvestor.core.utility.getHourAndMinute
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.history.domain.repository.IHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class HistoryRepository(
    private val conversationDao: ConversationDao,
    private val analyticsLogger: AnalyticsLogger
) :
    IHistoryRepository {
    override suspend fun getAllHistory(): Flow<Either<Failure, Map<String, List<StructuredConversation>>>> = flow {
        try {
            val separated = conversationDao.getConversationsWithMessages()
                .groupBy { it.conversation.timestamp.formatAsRelativeDate() }

            val conversations = separated.map { (date, entities) ->
                date to entities.map { entity ->
                    with(entity) {
                        StructuredConversation(
                            id = conversation.conversationId,
                            title = conversation.title,
                            lastMessageTime = conversation.timestamp.getHourAndMinute()
                        )
                    }
                }
            }
            emit(Either.Right(conversations.toMap()))
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
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
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }
}
