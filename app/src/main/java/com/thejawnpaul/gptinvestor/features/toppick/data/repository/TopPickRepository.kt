package com.thejawnpaul.gptinvestor.features.toppick.data.repository

import com.thejawnpaul.gptinvestor.analytics.composite.CompositeLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.features.toppick.data.local.dao.TopPickDao
import com.thejawnpaul.gptinvestor.features.toppick.data.local.model.TopPickEntity
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class TopPickRepository @Inject constructor(
    private val apiService: ApiService,
    private val topPickDao: TopPickDao,
    private val analyticsLogger: CompositeLogger,
    private val remoteConfig: RemoteConfig
) :
    ITopPickRepository {
    override suspend fun getTopPicks(): Flow<Either<Failure, List<TopPick>>> = flow {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            // emit local
            val local = topPickDao.getAllTopPicks().filter { it.date == today }.map { entity ->
                with(entity) {
                    TopPick(
                        id = id,
                        companyName = companyName,
                        ticker = ticker,
                        rationale = rationale,
                        metrics = metrics,
                        risks = risks,
                        confidenceScore = confidenceScore,
                        isSaved = isSaved
                    )
                }
            }.sortedByDescending { it.confidenceScore }

            if (local.isNotEmpty()) {
                Either.Right(local)
            }

            val response = apiService.getTopPicks(date = today)
            if (response.isSuccessful) {
                response.body()?.let {
                    val newPicks = it.map { aa ->
                        with(aa) {
                            TopPickEntity(
                                id = id,
                                companyName = companyName,
                                ticker = ticker,
                                rationale = rationale,
                                metrics = metrics,
                                risks = risks,
                                confidenceScore = confidenceScore,
                                date = date
                            )
                        }
                    }
                    topPickDao.replaceUnsavedWithNewPicks(newPicks)
                } ?: emit(Either.Left(Failure.DataError))
            }

            // emit local
            emit(
                Either.Right(
                    topPickDao.getAllTopPicks().filter { it.date == today }.map { entity ->
                        with(entity) {
                            TopPick(
                                id = id,
                                companyName = companyName,
                                ticker = ticker,
                                rationale = rationale,
                                metrics = metrics,
                                risks = risks,
                                confidenceScore = confidenceScore,
                                isSaved = isSaved
                            )
                        }
                    }.sortedByDescending { it.confidenceScore }
                )
            )
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getSingleTopPick(pickId: String): Flow<Either<Failure, TopPick>> = flow {
        try {
            val pick = with(topPickDao.getSingleTopPick(pickId)) {
                TopPick(
                    id,
                    companyName,
                    ticker,
                    rationale,
                    metrics,
                    risks,
                    confidenceScore,
                    isSaved
                )
            }
            emit(Either.Right(pick))
            analyticsLogger.logTopPickSelected(
                companyTicker = pick.ticker,
                companyName = pick.companyName
            )
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.UnAvailableError))
        }
    }

    override suspend fun saveTopPick(id: String): Flow<Either<Failure, TopPick>> = flow {
        try {
            val entity = topPickDao.getSingleTopPick(id).copy(isSaved = true)
            topPickDao.updateTopPick(entity)
            val pick = with(topPickDao.getSingleTopPick(id)) {
                TopPick(
                    id,
                    companyName,
                    ticker,
                    rationale,
                    metrics,
                    risks,
                    confidenceScore,
                    isSaved
                )
            }
            emit(Either.Right(pick))
            analyticsLogger.logSaveEvent(contentType = "top_pick", contentName = pick.companyName)
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun removeSavedTopPick(id: String): Flow<Either<Failure, TopPick>> = flow {
        try {
            val entity = topPickDao.getSingleTopPick(id).copy(isSaved = false)
            topPickDao.updateTopPick(entity)
            val pick = with(topPickDao.getSingleTopPick(id)) {
                TopPick(
                    id,
                    companyName,
                    ticker,
                    rationale,
                    metrics,
                    risks,
                    confidenceScore,
                    isSaved
                )
            }
            emit(Either.Right(pick))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun shareTopPick(id: String): Flow<Either<Failure, String>> = flow {
        try {
            val pick = topPickDao.getSingleTopPick(id)
            val domain = remoteConfig.fetchAndActivateStringValue("website_domain")

            val urlToShare = "${domain}single-pick/${pick.id}"

            val data = "\uD83D\uDCC8 Stock Pick Alert: ${pick.companyName}\n" +
                "\n" +
                "I just uncovered a high-potential opportunity using GPT Investor and had to share it with you.\n" +
                "\n" +
                "\uD83D\uDD0D See the full analysis here: ${urlToShare}\n" +
                "\n" +
                "Check it out and let me know what you think!"

            emit(Either.Right(data))

            analyticsLogger.logShareEvent(contentType = "top_pick", contentName = pick.companyName)
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
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
                                confidenceScore = confidenceScore,
                                isSaved = isSaved
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

    override suspend fun getLocalTopPicks(): Flow<Either<Failure, List<TopPick>>> = flow {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val local = topPickDao.getAllTopPicks().map { entity ->
            with(entity) {
                TopPick(
                    id = id,
                    companyName = companyName,
                    ticker = ticker,
                    rationale = rationale,
                    metrics = metrics,
                    risks = risks,
                    confidenceScore = confidenceScore,
                    isSaved = isSaved
                )
            }
        }.sortedByDescending { it.confidenceScore }
        emit(Either.Right(local))
    }
}
