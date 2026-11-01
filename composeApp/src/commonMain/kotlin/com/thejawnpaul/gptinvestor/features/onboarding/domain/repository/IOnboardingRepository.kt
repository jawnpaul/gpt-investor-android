package com.thejawnpaul.gptinvestor.features.onboarding.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingSuggestions

interface IOnboardingRepository {
    suspend fun fetchSuggestions(): Either<Failure, OnboardingSuggestions>
}
