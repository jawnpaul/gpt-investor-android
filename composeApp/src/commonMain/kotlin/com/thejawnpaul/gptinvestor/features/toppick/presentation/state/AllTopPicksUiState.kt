package com.thejawnpaul.gptinvestor.features.toppick.presentation.state

import com.thejawnpaul.gptinvestor.features.discover.picksForGoal
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation

data class AllTopPicksUiState(
    val topPicksView: TopPicksView = TopPicksView(),
    val savedGoal: InvestingGoal? = null,
    val selectedGoal: InvestingGoal? = null
) {
    val goalFilter: InvestingGoal
        get() = selectedGoal ?: savedGoal ?: InvestingGoal.JUST_CURIOUS

    private val goalPicks: List<TopPickPresentation> = picksForGoal(topPicksView.topPicks, goalFilter)

    val noPicksForGoal: Boolean
        get() = goalPicks.isEmpty() && topPicksView.topPicks.isNotEmpty()

    val shownPicks: List<TopPickPresentation>
        get() = goalPicks.ifEmpty { topPicksView.topPicks }

    val showGoalFilters: Boolean
        get() = topPicksView.topPicks.isNotEmpty()
}
