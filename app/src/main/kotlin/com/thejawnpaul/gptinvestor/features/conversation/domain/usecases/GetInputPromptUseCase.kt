package com.thejawnpaul.gptinvestor.features.conversation.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GetInputPromptUseCase(
    dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: IConversationRepository
) : BaseUseCase<ConversationPrompt, Conversation>(coroutineScope, dispatcher) {
    override suspend fun run(params: ConversationPrompt): Flow<Either<Failure, Conversation>> =
        repository.getInputResponse(params)
}
