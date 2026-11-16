package com.thejawnpaul.gptinvestor.features.onboarding.domain.model

data class QuestionState(val question: OnboardingQuestion, val answer: String?, val isAsked: Boolean) {
    val isSettled: Boolean
        get() = isAsked || answer != null
}
