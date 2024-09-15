package com.thejawnpaul.gptinvestor.features.conversation.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import kotlinx.coroutines.flow.Flow

interface IConversationRepository {

    suspend fun getDefaultPrompts(): Flow<Either<Failure, List<DefaultPrompt>>>
}
