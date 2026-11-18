package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.goal_filter_empty
import com.thejawnpaul.gptinvestor.goal_filter_for_you
import com.thejawnpaul.gptinvestor.goal_filter_growth
import com.thejawnpaul.gptinvestor.goal_filter_income
import com.thejawnpaul.gptinvestor.goal_filter_label
import com.thejawnpaul.gptinvestor.goal_filter_learning
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiChip
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val FILTER_GOALS = listOf(
    InvestingGoal.JUST_CURIOUS,
    InvestingGoal.GROW_WEALTH,
    InvestingGoal.GENERATE_INCOME,
    InvestingGoal.LEARN_BASICS
)

internal fun InvestingGoal.filterLabelRes(): StringResource = when (this) {
    InvestingGoal.GROW_WEALTH -> Res.string.goal_filter_growth
    InvestingGoal.GENERATE_INCOME -> Res.string.goal_filter_income
    InvestingGoal.LEARN_BASICS -> Res.string.goal_filter_learning
    InvestingGoal.JUST_CURIOUS -> Res.string.goal_filter_for_you
}

@Composable
internal fun GoalFilterChips(
    selected: InvestingGoal?,
    onSelect: (InvestingGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(Res.string.goal_filter_label)
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .selectableGroup()
            .semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)
    ) {
        FILTER_GOALS.forEach { goal ->
            GiChip(
                text = stringResource(goal.filterLabelRes()),
                onClick = { if (goal != selected) onSelect(goal) },
                selected = goal == selected,
                singleChoice = true
            )
        }
    }
}

@Composable
internal fun NoPicksForGoalNote(modifier: Modifier = Modifier) {
    GiText(
        text = stringResource(Res.string.goal_filter_empty),
        modifier = modifier,
        style = GiTextStyle.Caption,
        color = GiTheme.colors.muted
    )
}

internal fun revealEnter(reducedMotion: Boolean): EnterTransition = if (reducedMotion) {
    EnterTransition.None
} else {
    expandVertically(GiMotion.smooth(), expandFrom = Alignment.Top) +
        fadeIn(tween(GiMotion.BASE, easing = GiEasing.emphasised))
}

internal fun revealExit(reducedMotion: Boolean): ExitTransition = if (reducedMotion) {
    ExitTransition.None
} else {
    shrinkVertically(GiMotion.smooth(), shrinkTowards = Alignment.Top) +
        fadeOut(tween(GiMotion.FAST, easing = GiEasing.emphasised))
}
