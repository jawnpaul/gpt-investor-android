package com.thejawnpaul.gptinvestor.features.toppick.presentation.state

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import org.junit.Test

class AllTopPicksUiStateTest {

    private fun pick(id: String, vararg tags: String) = TopPickPresentation(
        id = id,
        companyName = id,
        ticker = id,
        rationale = "",
        metrics = emptyList(),
        risks = emptyList(),
        confidenceScore = 8,
        isSaved = false,
        percentageChange = 0f,
        currentPrice = 0f,
        goalTags = tags.toList()
    )

    private val picks = listOf(
        pick("GROW", "GROW_WEALTH"),
        pick("PAY", "GENERATE_INCOME"),
        pick("BOTH", "GROW_WEALTH", "GENERATE_INCOME")
    )

    @Test
    fun `with no goal every pick shows under For you`() {
        val state = AllTopPicksUiState(topPicksView = TopPicksView(topPicks = picks))

        assertThat(state.goalFilter).isEqualTo(InvestingGoal.JUST_CURIOUS)
        assertThat(state.shownPicks).isEqualTo(picks)
    }

    @Test
    fun `the saved goal filters the list until a chip is picked`() {
        val saved =
            AllTopPicksUiState(topPicksView = TopPicksView(topPicks = picks), savedGoal = InvestingGoal.GROW_WEALTH)
        val picked = saved.copy(selectedGoal = InvestingGoal.GENERATE_INCOME)

        assertThat(saved.shownPicks.map { it.id }).containsExactly("GROW", "BOTH").inOrder()
        assertThat(picked.shownPicks.map { it.id }).containsExactly("PAY", "BOTH").inOrder()
    }

    @Test
    fun `a goal with no picks shows every pick with a note`() {
        val state =
            AllTopPicksUiState(topPicksView = TopPicksView(topPicks = picks), selectedGoal = InvestingGoal.LEARN_BASICS)

        assertThat(state.noPicksForGoal).isTrue()
        assertThat(state.shownPicks).isEqualTo(picks)
    }

    @Test
    fun `chips show only when there are picks`() {
        assertThat(AllTopPicksUiState(topPicksView = TopPicksView(loading = true)).showGoalFilters).isFalse()
        assertThat(AllTopPicksUiState(topPicksView = TopPicksView(topPicks = picks)).showGoalFilters).isTrue()
    }
}
