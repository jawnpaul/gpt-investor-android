package com.thejawnpaul.gptinvestor.features.onboarding.data.repository

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.platform.InstallationIdProvider
import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.PickedStockRequest
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSuggestions
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.SuggestedStock
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingRepository
import kotlin.coroutines.cancellation.CancellationException
import org.koin.core.annotation.Singleton

@Singleton(binds = [IOnboardingRepository::class])
class OnboardingRepository(
    private val apiService: KtorApiService,
    private val installationIdProvider: InstallationIdProvider
) : IOnboardingRepository {

    override suspend fun fetchSuggestions(): Either<Failure, OnboardingSuggestions> = try {
        val response = apiService.getOnboardingSuggestions()
        val body = response.body
        if (response.isSuccessful && body != null && body.stocks.isNotEmpty()) {
            Either.Right(
                OnboardingSuggestions(
                    market = body.market,
                    stocks = body.stocks.map { SuggestedStock(it.ticker, it.name, it.logoUrl?.toHttpsUrl().orEmpty()) }
                )
            )
        } else {
            Either.Left(Failure.ServerError)
        }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Either.Left(Failure.NetworkConnection)
    }

    override suspend fun savePickedStock(ticker: String): Either<Failure, Unit> = try {
        val response = apiService.savePickedStock(installationIdProvider.get(), PickedStockRequest(ticker))
        if (response.isSuccessful) Either.Right(Unit) else Either.Left(Failure.ServerError)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Either.Left(Failure.NetworkConnection)
    }
}
