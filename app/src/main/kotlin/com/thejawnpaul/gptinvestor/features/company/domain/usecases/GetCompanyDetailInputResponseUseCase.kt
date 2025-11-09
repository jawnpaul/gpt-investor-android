package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompanyDetailInputResponseUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: IConversationRepository
) : BaseUseCase<CompanyPrompt, Conversation>(coroutineScope, dispatcher) {
    override suspend fun run(params: CompanyPrompt): Flow<Either<Failure, Conversation>> {
        return repository.getCompanyInputResponse(params)
    }
}
