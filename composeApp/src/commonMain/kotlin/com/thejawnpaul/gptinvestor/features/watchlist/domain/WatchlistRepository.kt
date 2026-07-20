package com.thejawnpaul.gptinvestor.features.watchlist.domain

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.ErrorResponse
import com.thejawnpaul.gptinvestor.features.watchlist.data.remote.model.AddWatchlistRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Singleton

interface WatchlistRepository {
    suspend fun addStockToWatchList(ticker: String): Either<Failure, Unit>
}

@Singleton(binds = [WatchlistRepository::class])
class WatchlistRepositoryImpl(private val apiService: KtorApiService) : WatchlistRepository {
    override suspend fun addStockToWatchList(ticker: String): Either<Failure, Unit> = try {
        val response = apiService.addToWatchlist(AddWatchlistRequest(ticker = ticker))
        if (response.isSuccessful) {
            Either.Right(Unit)
        } else {
            val errorBody = response.errorBody
            if (errorBody != null) {
                try {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    val errorCode = errorResponse.error
                    when (errorCode) {
                        "sign_up_required" -> Either.Left(WatchlistFailure.SignUpRequired)
                        "watchlist_full" -> {
                            val json = Json.parseToJsonElement(errorBody).jsonObject
                            val cap = json["cap"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                            Either.Left(WatchlistFailure.WatchlistFull(cap))
                        }
                        "ticker_not_found" -> Either.Left(WatchlistFailure.TickerNotFound)
                        else -> {
                            val json = Json.parseToJsonElement(errorBody).jsonObject
                            val message = json["message"]?.jsonPrimitive?.content
                                ?: errorCode
                            Either.Left(WatchlistFailure.GeneralError(message))
                        }
                    }
                } catch (e: Exception) {
                    Either.Left(Failure.ServerError)
                }
            } else {
                Either.Left(Failure.ServerError)
            }
        }
    } catch (e: Exception) {
        Either.Left(Failure.NetworkConnection)
    }
}
