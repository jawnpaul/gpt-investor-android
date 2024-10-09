package com.thejawnpaul.gptinvestor.features.history.data.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.history.domain.repository.IHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class HistoryRepository @Inject constructor(private val conversationDao: ConversationDao) :
    IHistoryRepository {
    override suspend fun getAllHistory(): Flow<Either<Failure, List<StructuredConversation>>> =
        flow {
            try {

                val conversations = conversationDao.getAllConversations().map { entity ->
                    with(entity) {
                        StructuredConversation(id = conversationId, title = title)
                    }
                }
                emit(Either.Right(conversations))
            } catch (e: Exception) {
                Timber.e(e.stackTraceToString())
                emit(Either.Left(Failure.DataError))
            }
        }
}