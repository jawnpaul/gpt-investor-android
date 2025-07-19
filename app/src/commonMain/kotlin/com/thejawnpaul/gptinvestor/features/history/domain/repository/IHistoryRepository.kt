package com.thejawnpaul.gptinvestor.features.history.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import kotlinx.coroutines.flow.Flow

interface IHistoryRepository {
    suspend fun getAllHistory(): Flow<Either<Failure, Map<String, List<StructuredConversation>>>>

    suspend fun getSingleHistory(id: Long): Flow<Either<Failure, StructuredConversation>>
}
