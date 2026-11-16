package com.thejawnpaul.gptinvestor.features.onboarding.domain.model

enum class InvestingExperience {
    BEGINNER,
    INTERMEDIATE,
    EXPERIENCED;

    companion object {
        fun fromAnswer(answer: String?): InvestingExperience? = entries.firstOrNull { it.name == answer }
    }
}
