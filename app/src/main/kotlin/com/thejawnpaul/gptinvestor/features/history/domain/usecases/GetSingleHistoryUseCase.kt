package com.thejawnpaul.gptinvestor.features.history.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.history.domain.repository.IHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSingleHistoryUseCase @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher, coroutineScope: CoroutineScope, private val repository: IHistoryRepository) :
    BaseUseCase<Long, StructuredConversation>(
        coroutineScope,
        dispatcher
    ) {

    override suspend fun run(params: Long): Flow<Either<Failure, StructuredConversation>> = repository.getSingleHistory(params)
}
