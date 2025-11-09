package com.thejawnpaul.gptinvestor.features.conversation.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import kotlinx.coroutines.flow.Flow

interface IConversationRepository {

    suspend fun getDefaultPrompts(): Flow<Either<Failure, List<DefaultPrompt>>>

    suspend fun getDefaultPromptResponse(prompt: DefaultPrompt): Flow<Either<Failure, Conversation>>

    suspend fun getInputResponse(prompt: ConversationPrompt): Flow<Either<Failure, Conversation>>

    suspend fun getCompanyInputResponse(prompt: CompanyPrompt): Flow<Either<Failure, Conversation>>
}
