package com.thejawnpaul.gptinvestor.features.history.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.history.domain.repository.IHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GetAllHistoryUseCase(
    dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: IHistoryRepository
) :
    BaseUseCase<GetAllHistoryUseCase.None, Map<String, List<StructuredConversation>>>(
        coroutineScope,
        dispatcher
    ) {
    class None

    override suspend fun run(params: None): Flow<Either<Failure, Map<String, List<StructuredConversation>>>> =
        repository.getAllHistory()
}
