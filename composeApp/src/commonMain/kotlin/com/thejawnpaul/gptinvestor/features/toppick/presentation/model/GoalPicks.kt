package com.thejawnpaul.gptinvestor.features.toppick.presentation.model

import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal

internal fun picksForGoal(picks: List<TopPickPresentation>, goal: InvestingGoal?): List<TopPickPresentation> =
    if (goal == null || goal == InvestingGoal.JUST_CURIOUS) picks else picks.filter { goal.name in it.goalTags }
