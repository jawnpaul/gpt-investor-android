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
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class GetAllHistoryUseCase(
    @Provided @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    @Provided coroutineScope: CoroutineScope,
    private val repository: IHistoryRepository
) : BaseUseCase<GetAllHistoryUseCase.None, Map<String, List<StructuredConversation>>>(
    coroutineScope,
    dispatcher
) {
    class None

    override suspend fun run(params: None): Flow<Either<Failure, Map<String, List<StructuredConversation>>>> =
        repository.getAllHistory()
}
