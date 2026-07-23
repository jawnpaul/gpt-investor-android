package com.thejawnpaul.gptinvestor.features.watchlist.domain

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model.AddWatchlistRequest
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistData
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistItem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Singleton

interface WatchlistRepository {
    suspend fun addStockToWatchList(ticker: String): Either<Failure, Unit>
    suspend fun getWatchlist(): Either<Failure, WatchlistData>
    suspend fun removeFromWatchlist(ticker: String): Either<Failure, Unit>
}

@Singleton(binds = [WatchlistRepository::class])
class WatchlistRepositoryImpl(private val apiService: KtorApiService) : WatchlistRepository {
    override suspend fun addStockToWatchList(ticker: String): Either<Failure, Unit> = try {
        val response = apiService.addToWatchlist(AddWatchlistRequest(ticker = ticker))
        if (response.isSuccessful) {
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
            val items = response.body?.items?.map {
                WatchlistItem(
                    ticker = it.ticker,
                    companyName = it.companyName,
                    dateAdded = it.dateAdded,
                    locked = it.locked,
                    logoUrl = it.logoUrl
                )
            } ?: emptyList()
            val recommendations = response.body?.recommendedDigestCompany ?: emptyList()
            Either.Right(WatchlistData(items = items, recommendations = recommendations))
        } else {
            Either.Left(Failure.ServerError)
        }
    } catch (e: Exception) {
        Either.Left(Failure.NetworkConnection)
    }

    override suspend fun removeFromWatchlist(ticker: String): Either<Failure, Unit> = try {
        val response = apiService.removeFromWatchlist(ticker)
        if (response.isSuccessful) {
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
            val cap = errorBody?.let {
                runCatching {
                    Json.parseToJsonElement(it).jsonObject["cap"]?.jsonPrimitive?.content?.toIntOrNull()
                }.getOrNull()
            } ?: 0
            Either.Left(WatchlistFailure.WatchlistFull(cap))
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
}
