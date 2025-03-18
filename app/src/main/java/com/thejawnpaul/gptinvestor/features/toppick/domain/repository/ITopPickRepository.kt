package com.thejawnpaul.gptinvestor.features.toppick.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import kotlinx.coroutines.flow.Flow

interface ITopPickRepository {

    suspend fun getTopPicks(): Flow<Either<Failure, List<TopPick>>>

    suspend fun getSingleTopPick(pickId: String): Flow<Either<Failure, TopPick>>

    suspend fun saveTopPick(id: String): Flow<Either<Failure, TopPick>>

    suspend fun removeSavedTopPick(id: String): Flow<Either<Failure, TopPick>>

    suspend fun shareTopPick(id: String): Flow<Either<Failure, String>>

    suspend fun getSavedTopPicks(): Flow<Either<Failure, List<TopPick>>>

    suspend fun getLocalTopPicks(): Flow<Either<Failure, List<TopPick>>>
}
