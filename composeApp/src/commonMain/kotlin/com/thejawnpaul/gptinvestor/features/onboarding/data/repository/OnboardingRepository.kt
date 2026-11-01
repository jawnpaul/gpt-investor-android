package com.thejawnpaul.gptinvestor.features.onboarding.data.repository

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.onboarding.data.remote.OnboardingSuggestionsRemote
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSuggestions
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.SuggestedStock
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Singleton

@Singleton(binds = [IOnboardingRepository::class])
class OnboardingRepository(private val apiService: KtorApiService, private val appPreferences: AppPreferences) :
    IOnboardingRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getSavedSuggestions(): OnboardingSuggestions? {
        val saved = appPreferences.onboardingSuggestions.first() ?: return null
        return try {
            json.decodeFromString<OnboardingSuggestionsRemote>(saved).toSuggestions().takeIf { it.stocks.isNotEmpty() }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
    }

    override suspend fun fetchSuggestions(): Either<Failure, OnboardingSuggestions> = try {
        val response = apiService.getOnboardingSuggestions()
        val body = response.body
        if (response.isSuccessful && body != null && body.stocks.isNotEmpty()) {
            appPreferences.setOnboardingSuggestions(json.encodeToString(body))
            Either.Right(body.toSuggestions())
        } else {
            Either.Left(Failure.ServerError)
        }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Either.Left(Failure.NetworkConnection)
    }

    private fun OnboardingSuggestionsRemote.toSuggestions() = OnboardingSuggestions(
        market = market,
        stocks = stocks.map { SuggestedStock(it.ticker, it.name, it.logoUrl?.toHttpsUrl().orEmpty()) }
    )
}
