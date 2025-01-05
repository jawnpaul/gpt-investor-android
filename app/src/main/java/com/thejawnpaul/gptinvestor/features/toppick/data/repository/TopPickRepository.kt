package com.thejawnpaul.gptinvestor.features.toppick.data.repository

import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.toppick.data.local.dao.TopPickDao
import com.thejawnpaul.gptinvestor.features.toppick.data.local.model.TopPickEntity
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class TopPickRepository @Inject constructor(
    private val apiService: ApiService,
    private val topPickDao: TopPickDao
) :
    ITopPickRepository {
    override suspend fun getTopPicks(): Flow<Either<Failure, List<TopPick>>> = flow {
        try {
            // emit local
            val local = topPickDao.getAllTopPicks().map { entity ->
                with(entity) {
                    TopPick(
                        id = id,
                        companyName = companyName,
                        ticker = ticker,
                        rationale = rationale,
                        metrics = metrics,
                        risks = risks,
                        confidenceScore = confidenceScore
                    )
                }
            }

            if (local.isNotEmpty()) {
                Either.Right(local)
            }

            val response = apiService.getTopPicks()
            if (response.isSuccessful) {
                response.body()?.let {
                    val newPicks = it.map { aa ->
                        with(aa) {
                            TopPickEntity(
                                companyName = companyName,
                                ticker = ticker,
                                rationale = rationale,
                                metrics = metrics,
                                risks = risks,
                                confidenceScore = confidenceScore
                            )
                        }
                    }
                    topPickDao.replaceUnsavedWithNewPicks(newPicks)
                } ?: emit(Either.Left(Failure.DataError))
            }

            // emit local
            emit(
                Either.Right(
                    topPickDao.getAllTopPicks().map { entity ->
                        with(entity) {
                            TopPick(
                                id = id,
                                companyName = companyName,
                                ticker = ticker,
                                rationale = rationale,
                                metrics = metrics,
                                risks = risks,
                                confidenceScore = confidenceScore
                            )
                        }
                    }
                )
            )
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getSingleTopPick(id: Long): Flow<Either<Failure, TopPick>> = flow {
        try {
            val pick = with(topPickDao.getSingleTopPick(id)) {
                TopPick(id, companyName, ticker, rationale, metrics, risks, confidenceScore)
            }
            emit(Either.Right(pick))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.UnAvailableError))
        }
    }

    override suspend fun saveTopPick(id: Long): Flow<Either<Failure, Unit>> = flow {
        try {
            val entity = topPickDao.getSingleTopPick(id).copy(isSaved = true)
            topPickDao.updateTopPick(entity)
            emit(Either.Right(Unit))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun removeSavedTopPick(id: Long): Flow<Either<Failure, Unit>> = flow {
        try {
            val entity = topPickDao.getSingleTopPick(id).copy(isSaved = false)
            topPickDao.updateTopPick(entity)
            emit(Either.Right(Unit))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun shareTopPick(id: Long): Flow<Either<Failure, String>> = flow {
        // TODO: Log share event to firebase
    }

    override suspend fun getSavedTopPicks(): Flow<Either<Failure, List<TopPick>>> = flow {
        try {
            emit(
                Either.Right(
                    topPickDao.getSavedTopPicks().map { entity ->
                        with(entity) {
                            TopPick(
                                id = id,
                                companyName = companyName,
                                ticker = ticker,
                                rationale = rationale,
                                metrics = metrics,
                                risks = risks,
                                confidenceScore = confidenceScore
                            )
                        }
                    }
                )
            )
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }
}
