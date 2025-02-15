package com.thejawnpaul.gptinvestor.features.toppick.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import kotlinx.coroutines.flow.Flow

interface ITopPickRepository {

    suspend fun getTopPicks(): Flow<Either<Failure, List<TopPick>>>

    suspend fun getSingleTopPick(pickId: Long): Flow<Either<Failure, TopPick>>

    suspend fun saveTopPick(id: Long): Flow<Either<Failure, TopPick>>

    suspend fun removeSavedTopPick(id: Long): Flow<Either<Failure, TopPick>>

    suspend fun shareTopPick(id: Long): Flow<Either<Failure, String>>

    suspend fun getSavedTopPicks(): Flow<Either<Failure, List<TopPick>>>

    suspend fun getLocalTopPicks(): Flow<Either<Failure, List<TopPick>>>
}
