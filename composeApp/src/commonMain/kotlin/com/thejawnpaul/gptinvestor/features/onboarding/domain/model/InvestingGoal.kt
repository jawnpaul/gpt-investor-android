package com.thejawnpaul.gptinvestor.features.onboarding.domain.model

enum class InvestingGoal {
    GROW_WEALTH,
    GENERATE_INCOME,
    LEARN_BASICS,
    JUST_CURIOUS;

    companion object {
        fun fromAnswer(answer: String?): InvestingGoal? = entries.firstOrNull { it.name == answer }
    }
}
