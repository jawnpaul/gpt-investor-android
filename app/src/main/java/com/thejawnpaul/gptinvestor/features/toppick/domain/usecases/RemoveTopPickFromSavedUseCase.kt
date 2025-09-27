package com.thejawnpaul.gptinvestor.features.toppick.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveTopPickFromSavedUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: ITopPickRepository
) : BaseUseCase<String, TopPick>(
    coroutineScope,
    dispatcher
) {

    override suspend fun run(params: String): Flow<Either<Failure, TopPick>> = repository.removeSavedTopPick(params)
}
