package com.thejawnpaul.gptinvestor.features.discover

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import org.junit.Test

class DiscoverGoalFilterTest {

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
        pick("BOTH", "GROW_WEALTH", "GENERATE_INCOME"),
        pick("NONE")
    )

    @Test
    fun `no goal or just curious shows every pick`() {
        assertThat(picksForGoal(picks, null)).isEqualTo(picks)
        assertThat(picksForGoal(picks, InvestingGoal.JUST_CURIOUS)).isEqualTo(picks)
    }

    @Test
    fun `a goal shows the picks tagged with it`() {
        assertThat(
            picksForGoal(picks, InvestingGoal.GENERATE_INCOME).map {
                it.id
            }
        ).containsExactly("PAY", "BOTH").inOrder()
    }

    @Test
    fun `a goal with no picks today falls back to all picks with a note`() {
        val state =
            DiscoveryScreenState(
                topPicksView = TopPicksView(topPicks = picks),
                savedGoal = InvestingGoal.LEARN_BASICS
            )

        assertThat(state.noPicksForGoal).isTrue()
        assertThat(state.previewTopPicks.map { it.id }).containsExactly("GROW", "PAY").inOrder()
    }

    @Test
    fun `filter chips appear once a goal is chosen and the banner is gone`() {
        assertThat(DiscoveryScreenState(savedGoal = null).showGoalFilters).isFalse()
        assertThat(
            DiscoveryScreenState(savedGoal = InvestingGoal.GROW_WEALTH, showGoalBanner = true).showGoalFilters
        ).isFalse()
        assertThat(
            DiscoveryScreenState(
                topPicksView = TopPicksView(topPicks = listOf(pick("a"))),
                savedGoal = InvestingGoal.GROW_WEALTH
            ).showGoalFilters
        ).isTrue()
    }

    @Test
    fun `top picks section shows while loading, on error or with picks, and hides when empty`() {
        assertThat(DiscoveryScreenState().showTopPicksSection).isFalse()
        assertThat(DiscoveryScreenState(topPicksView = TopPicksView(loading = true)).showTopPicksSection).isTrue()
        assertThat(DiscoveryScreenState(topPicksView = TopPicksView(error = "x")).showTopPicksSection).isTrue()
        assertThat(
            DiscoveryScreenState(topPicksView = TopPicksView(topPicks = listOf(pick("a")))).showTopPicksSection
        ).isTrue()
    }

    @Test
    fun `filter chips are hidden while there are no picks`() {
        assertThat(DiscoveryScreenState(savedGoal = InvestingGoal.GROW_WEALTH).showGoalFilters).isFalse()
    }

    @Test
    fun `a chip filters without replacing the saved goal`() {
        val state = DiscoveryScreenState(
            topPicksView = TopPicksView(topPicks = picks),
            savedGoal = InvestingGoal.GROW_WEALTH,
            selectedGoalFilter = InvestingGoal.GENERATE_INCOME
        )

        assertThat(state.goalFilter).isEqualTo(InvestingGoal.GENERATE_INCOME)
        assertThat(state.savedGoal).isEqualTo(InvestingGoal.GROW_WEALTH)
        assertThat(state.previewTopPicks.map { it.id }).containsExactly("PAY", "BOTH").inOrder()
    }
}
