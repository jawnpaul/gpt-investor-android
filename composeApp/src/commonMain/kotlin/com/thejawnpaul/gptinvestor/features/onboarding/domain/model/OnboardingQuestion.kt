package com.thejawnpaul.gptinvestor.features.onboarding.domain.model

enum class AskTrigger {
    HOME,
    DISCOVER,
    WATCHLIST_ADD
}

enum class OnboardingQuestion(val id: String, val trigger: AskTrigger, val askGuests: Boolean) {
    EXPERIENCE("experience", AskTrigger.HOME, askGuests = false),
    GOAL("goal", AskTrigger.DISCOVER, askGuests = true),
    ALERTS("alerts", AskTrigger.WATCHLIST_ADD, askGuests = false)
}
