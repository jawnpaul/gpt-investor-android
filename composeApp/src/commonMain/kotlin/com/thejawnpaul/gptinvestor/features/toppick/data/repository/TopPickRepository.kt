package com.thejawnpaul.gptinvestor.features.toppick.data.repository

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfigClient
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.toppick.data.local.dao.TopPickDao
import com.thejawnpaul.gptinvestor.features.toppick.data.local.model.TopPickEntity
import com.thejawnpaul.gptinvestor.features.toppick.domain.model.TopPick
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

@Singleton(binds = [ITopPickRepository::class])
class TopPickRepository(
    private val apiService: KtorApiService,
    private val topPickDao: TopPickDao,
    @Provided private val analyticsLogger: AnalyticsLogger,
    private val remoteConfig: RemoteConfigClient
) : ITopPickRepository {
    override suspend fun getTopPicks(): Flow<Either<Failure, List<TopPick>>> = flow {
        try {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

            val response = apiService.getTopPicks(date = today)
            if (response.isSuccessful) {
                response.body?.let { remotePick ->
                    val topPickEntities = remotePick.map { aa ->
                        with(aa) {
                            TopPickEntity(
                                id = id,
                                companyName = companyName,
                                ticker = ticker,
                                rationale = rationale,
                                metrics = metrics,
                                risks = risks,
                                confidenceScore = confidenceScore,
                                date = date,
                                price = price ?: 0.0f,
                                change = percentageChange ?: 0.0f
                            )
                        }
                    }
                    topPickDao.replaceUnsavedWithNewPicks(topPickEntities)
                    val picks = remotePick.map { pickEntity ->
                        with(pickEntity) {
                            TopPick(
                                id = id,
                                companyName = companyName,
                                ticker = ticker,
                                rationale = rationale,
                                metrics = metrics,
                                risks = risks,
                                confidenceScore = confidenceScore,
                                isSaved = false,
                                imageUrl = imageUrl?.toHttpsUrl() ?: "",
                                percentageChange = percentageChange ?: 0f,
                                currentPrice = 0f
                            )
                        }
                    }
                    emit(Either.Right(picks))
                } ?: emit(Either.Left(Failure.DataError))
            }
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getSingleTopPick(pickId: String): Flow<Either<Failure, TopPick>> = flow {
        try {
            val pick = with(topPickDao.getSingleTopPick(pickId)) {
                TopPick(
                    id = id,
                    companyName = companyName,
                    ticker = ticker,
                    rationale = rationale,
                    metrics = metrics,
                    risks = risks,
                    confidenceScore = confidenceScore,
                    isSaved = isSaved,
                    imageUrl = imageUrl,
                    percentageChange = change,
                    currentPrice = price
                )
            }
            emit(Either.Right(pick))
            analyticsLogger.logEvent(
                eventName = "top-pick-selected",
                params = mapOf("company_ticker" to pick.ticker, "company_name" to pick.companyName)
            )
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.UnAvailableError))
        }
    }

    override suspend fun saveTopPick(id: String): Flow<Either<Failure, TopPick>> = flow {
        try {
            val entity = topPickDao.getSingleTopPick(id).copy(isSaved = true)
            topPickDao.updateTopPick(entity)
            val pick = with(topPickDao.getSingleTopPick(id)) {
                TopPick(
                    id = id,
                    companyName = companyName,
                    ticker = ticker,
                    rationale = rationale,
                    metrics = metrics,
                    risks = risks,
                    confidenceScore = confidenceScore,
                    isSaved = isSaved,
                    imageUrl = imageUrl,
                    percentageChange = change,
                    currentPrice = price
                )
            }
            emit(Either.Right(pick))
            analyticsLogger.logEvent(
                eventName = "save",
                params = mapOf("content_type" to "top_pick", "company_ticker" to pick.ticker)
            )
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun removeSavedTopPick(id: String): Flow<Either<Failure, TopPick>> = flow {
        try {
            val entity = topPickDao.getSingleTopPick(id).copy(isSaved = false)
            topPickDao.updateTopPick(entity)
            val pick = with(topPickDao.getSingleTopPick(id)) {
                TopPick(
                    id = id,
                    companyName = companyName,
                    ticker = ticker,
                    rationale = rationale,
                    metrics = metrics,
                    risks = risks,
                    confidenceScore = confidenceScore,
                    isSaved = isSaved,
                    imageUrl = imageUrl,
                    percentageChange = change,
                    currentPrice = price
                )
            }
            emit(Either.Right(pick))
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun shareTopPick(id: String): Flow<Either<Failure, String>> = flow {
        try {
            val pick = topPickDao.getSingleTopPick(id)
            val domain = remoteConfig.fetchAndActivateStringValue(Constants.WEBSITE_DOMAIN_KEY)

            val urlToShare = "${domain}single-pick/${pick.id}"

            val data = "\uD83D\uDCC8 Stock Pick Alert: ${pick.companyName}\n" +
                "\n" +
                "I just uncovered a high-potential opportunity using GPT Investor and had to share it with you.\n" +
                "\n" +
                "\uD83D\uDD0D See the full analysis here: ${urlToShare}\n" +
                "\n" +
                "Check it out and let me know what you think!"

            emit(Either.Right(data))

            analyticsLogger.logEvent(
                eventName = "share",
                params = mapOf(
                    "content_type" to "top_pick",
                    "company_name" to pick.companyName,
                    "company_ticker" to pick.ticker
                )
            )
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
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
                                isSaved = isSaved,
                                imageUrl = imageUrl,
                                percentageChange = change,
                                currentPrice = price

                            )
                        }
                    }
                )
            )
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun getLocalTopPicks(): Flow<Either<Failure, List<TopPick>>> = flow {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
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
                    isSaved = isSaved,
                    imageUrl = imageUrl,
                    percentageChange = change,
                    currentPrice = price
                )
            }
        }.sortedByDescending { it.confidenceScore }
        emit(Either.Right(local))
    }

    override suspend fun getTopPicksByDate(): Flow<List<TopPick>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        return try {
            topPickDao.getTopPicksFlow(today).map { list ->
                list.map { entity ->
                    with(entity) {
                        TopPick(
                            id = id,
                            companyName = companyName,
                            ticker = ticker,
                            rationale = rationale,
                            metrics = metrics,
                            risks = risks,
                            confidenceScore = confidenceScore,
                            isSaved = isSaved,
                            imageUrl = imageUrl,
                            percentageChange = change,
                            currentPrice = price
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            flow { emit(emptyList()) }
        }
    }

    override suspend fun searchTopPicks(query: String): Flow<List<TopPick>> = try {
        topPickDao.searchTopPicks(query).map { list ->
            list.map { entity ->
                with(entity) {
                    TopPick(
                        id = id,
                        companyName = companyName,
                        ticker = ticker,
                        rationale = rationale,
                        metrics = metrics,
                        risks = risks,
                        confidenceScore = confidenceScore,
                        isSaved = isSaved,
                        imageUrl = imageUrl,
                        percentageChange = change,
                        currentPrice = price
                    )
                }
            }
        }
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        flow { emit(emptyList()) }
    }
}
