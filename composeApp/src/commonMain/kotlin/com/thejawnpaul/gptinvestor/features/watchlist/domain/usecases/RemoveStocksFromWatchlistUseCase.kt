package com.thejawnpaul.gptinvestor.features.watchlist.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class RemoveStocksFromWatchlistUseCase(
    @Provided @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    @Provided coroutineScope: CoroutineScope,
    private val repository: WatchlistRepository
) : BaseUseCase<List<String>, List<String>>(coroutineScope, dispatcher) {

    override suspend fun run(params: List<String>): Flow<Either<Failure, List<String>>> =
        flow { emit(repository.removeStocksFromWatchlist(params)) }
}
