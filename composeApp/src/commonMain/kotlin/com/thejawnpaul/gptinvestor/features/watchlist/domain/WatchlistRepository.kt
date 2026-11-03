package com.thejawnpaul.gptinvestor.features.watchlist.domain

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.utility.DEFAULT_CURRENCY_SYMBOL
import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model.AddWatchlistRequest
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistData
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistItem
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.watchlistBriefStatusOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

interface WatchlistRepository {
    /** Emits after every successful add or remove, from any screen. */
    val changes: Flow<Unit>
    suspend fun addStockToWatchList(ticker: String): Either<Failure, Unit>
    suspend fun getWatchlist(): Either<Failure, WatchlistData>
    suspend fun removeFromWatchlist(ticker: String): Either<Failure, Unit>
}

@Singleton(binds = [WatchlistRepository::class])
class WatchlistRepositoryImpl(
    private val apiService: KtorApiService,
    @Provided private val analyticsLogger: AnalyticsLogger
) : WatchlistRepository {
    private val _changes =
        MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val changes: Flow<Unit> = _changes.asSharedFlow()

    override suspend fun addStockToWatchList(ticker: String): Either<Failure, Unit> = try {
        val response = apiService.addToWatchlist(AddWatchlistRequest(ticker = ticker))
        if (response.isSuccessful) {
            _changes.tryEmit(Unit)
            Either.Right(Unit)
        } else {
            mapErrorResponse(response.code, response.errorBody)
        }
    } catch (e: Exception) {
        Either.Left(Failure.NetworkConnection)
    }

    override suspend fun getWatchlist(): Either<Failure, WatchlistData> = try {
        val response = apiService.getWatchlist()
        if (response.isSuccessful) {
            val cap = response.body?.cap
            if (response.body?.plan == PLAN_FREE && cap != null) {
                analyticsLogger.setUserProperty(USER_PROPERTY_FREE_WATCHLIST_LIMIT, cap.toString())
            }
            val items = response.body?.items?.map {
                WatchlistItem(
                    ticker = it.ticker,
                    companyName = it.companyName,
                    dateAdded = it.dateAdded,
                    locked = it.locked,
                    logoUrl = it.logoUrl.toHttpsUrl(),
                    currentPrice = it.currentPrice,
                    priceChange = it.priceChange,
                    currencySymbol = it.currencySymbol ?: DEFAULT_CURRENCY_SYMBOL,
                    briefStatus = watchlistBriefStatusOf(it.digestStatus, it.locked),
                    market = it.market
                )
            } ?: emptyList()
            val recommendations = response.body?.recommendedDigestCompany ?: emptyList()
            Either.Right(WatchlistData(items = items, recommendations = recommendations))
        } else if (response.code == 403) {
            Either.Left(WatchlistFailure.SignUpRequired)
        } else {
            Either.Left(Failure.ServerError)
        }
    } catch (e: Exception) {
        Either.Left(Failure.NetworkConnection)
    }

    override suspend fun removeFromWatchlist(ticker: String): Either<Failure, Unit> = try {
        val response = apiService.removeFromWatchlist(ticker)
        if (response.isSuccessful) {
            _changes.tryEmit(Unit)
            Either.Right(Unit)
        } else {
            when (response.code) {
                404 -> Either.Left(WatchlistFailure.TickerNotFound)
                else -> Either.Left(Failure.ServerError)
            }
        }
    } catch (e: Exception) {
        Either.Left(Failure.NetworkConnection)
    }

    private fun mapErrorResponse(code: Int, errorBody: String?): Either.Left<Failure> = when (code) {
        401 -> Either.Left(Failure.ServerError)
        403 -> Either.Left(WatchlistFailure.SignUpRequired)
        404 -> Either.Left(WatchlistFailure.TickerNotFound)
        409 -> {
            val body = errorBody?.let { runCatching { Json.parseToJsonElement(it).jsonObject }.getOrNull() }
            val cap = body?.get("cap")?.jsonPrimitive?.content?.toIntOrNull()
            if (body?.get("error")?.jsonPrimitive?.content == ERROR_WATCHLIST_FULL) {
                logWatchlistFull(cap)
            }
            Either.Left(WatchlistFailure.WatchlistFull(cap ?: 0))
        }
        400 -> {
            val message = errorBody?.let {
                runCatching {
                    Json.parseToJsonElement(it).jsonObject["error"]?.jsonPrimitive?.content
                }.getOrNull()
            } ?: errorBody
            Either.Left(WatchlistFailure.GeneralError(message ?: "Bad request"))
        }
        else -> Either.Left(Failure.ServerError)
    }

    private fun logWatchlistFull(cap: Int?) {
        if (cap == null) {
            analyticsLogger.logEvent(EVENT_WATCHLIST_FULL, emptyMap())
            return
        }
        analyticsLogger.logEvent(EVENT_WATCHLIST_FULL, mapOf("cap" to cap))
        analyticsLogger.setUserProperty(USER_PROPERTY_FREE_WATCHLIST_LIMIT, cap.toString())
    }

    private companion object {
        const val PLAN_FREE = "free"
        const val ERROR_WATCHLIST_FULL = "watchlist_full"
        const val EVENT_WATCHLIST_FULL = "watchlist_full"
        const val USER_PROPERTY_FREE_WATCHLIST_LIMIT = "free_watchlist_limit"
    }
}
