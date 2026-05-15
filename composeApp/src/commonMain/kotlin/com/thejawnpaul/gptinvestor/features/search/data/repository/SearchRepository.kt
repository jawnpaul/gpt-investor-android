package com.thejawnpaul.gptinvestor.features.search.data.repository

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.search.data.remote.toDomain
import com.thejawnpaul.gptinvestor.features.search.domain.model.SearchSection
import com.thejawnpaul.gptinvestor.features.search.domain.repository.ISearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Singleton

@Singleton(binds = [ISearchRepository::class])
class SearchRepository(private val apiService: KtorApiService) : ISearchRepository {

    override suspend fun search(query: String?): Flow<Either<Failure, List<SearchSection>>> = flow {
        try {
            val response = apiService.getSearchResults(query)
            if (response.isSuccessful) {
                val sections = response.body?.sections?.mapNotNull { it.toDomain() } ?: emptyList()
                emit(Either.Right(sections))
            } else {
                emit(Either.Left(Failure.ServerError))
            }
        } catch (e: Exception) {
            emit(Either.Left(Failure.NetworkConnection))
        }
    }

    override suspend fun clearHistory(): Flow<Either<Failure, Unit>> = flow {
        try {
            val response = apiService.clearSearchHistory()
            if (response.isSuccessful) {
                emit(Either.Right(Unit))
            } else {
                emit(Either.Left(Failure.ServerError))
            }
        } catch (e: Exception) {
            emit(Either.Left(Failure.NetworkConnection))
        }
    }
}
