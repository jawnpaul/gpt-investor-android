package com.thejawnpaul.gptinvestor.features.onboarding.domain.model

import com.thejawnpaul.gptinvestor.core.functional.Failure

sealed class OnboardingQuestionFailure : Failure.FeatureFailure() {
    data object NothingToAsk : OnboardingQuestionFailure()
}
