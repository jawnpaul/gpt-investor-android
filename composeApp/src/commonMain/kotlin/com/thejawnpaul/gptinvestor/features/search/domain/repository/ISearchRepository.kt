package com.thejawnpaul.gptinvestor.features.search.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.search.domain.model.SearchSection
import kotlinx.coroutines.flow.Flow

interface ISearchRepository {
    suspend fun search(query: String?): Flow<Either<Failure, List<SearchSection>>>
    suspend fun clearHistory(): Flow<Either<Failure, Unit>>
}
