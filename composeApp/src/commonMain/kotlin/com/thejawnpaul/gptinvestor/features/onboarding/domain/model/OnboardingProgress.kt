package com.thejawnpaul.gptinvestor.features.onboarding.domain.model

data class OnboardingProgress(
    val owner: String = "",
    val answers: Map<String, String> = emptyMap(),
    val asked: Set<String> = emptySet()
) {
    fun stateOf(question: OnboardingQuestion) =
        QuestionState(question = question, answer = answers[question.id], isAsked = question.id in asked)
}

data class OnboardingSync(val progress: OnboardingProgress, val addedStock: String?)
