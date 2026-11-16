package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.goal_banner_subtitle
import com.thejawnpaul.gptinvestor.goal_banner_title
import com.thejawnpaul.gptinvestor.goal_filter_empty
import com.thejawnpaul.gptinvestor.goal_filter_for_you
import com.thejawnpaul.gptinvestor.goal_filter_growth
import com.thejawnpaul.gptinvestor.goal_filter_income
import com.thejawnpaul.gptinvestor.goal_filter_label
import com.thejawnpaul.gptinvestor.goal_filter_learning
import com.thejawnpaul.gptinvestor.not_now
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiChip
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val FILTER_GOALS = listOf(
    InvestingGoal.JUST_CURIOUS,
    InvestingGoal.GROW_WEALTH,
    InvestingGoal.GENERATE_INCOME,
    InvestingGoal.LEARN_BASICS
)

private fun InvestingGoal.filterLabelRes(): StringResource = when (this) {
    InvestingGoal.GROW_WEALTH -> Res.string.goal_filter_growth
    InvestingGoal.GENERATE_INCOME -> Res.string.goal_filter_income
    InvestingGoal.LEARN_BASICS -> Res.string.goal_filter_learning
    InvestingGoal.JUST_CURIOUS -> Res.string.goal_filter_for_you
}

@Composable
internal fun GoalBanner(onSelect: (InvestingGoal) -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(GiRadius.md)
            .background(colors.violetSoft)
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x0_5)) {
                GiText(
                    text = stringResource(Res.string.goal_banner_title),
                    modifier = Modifier.semantics { heading() },
                    style = GiTextStyle.Link,
                    color = colors.ink
                )
                GiText(
                    text = stringResource(Res.string.goal_banner_subtitle),
                    style = GiTextStyle.Caption,
                    color = colors.body
                )
            }
            GiButton(
                text = stringResource(Res.string.not_now),
                onClick = onDismiss,
                size = GiButtonSize.Small,
                variant = GiButtonVariant.Text
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            FILTER_GOALS.forEach { goal ->
                GiChip(text = stringResource(goal.filterLabelRes()), onClick = { onSelect(goal) })
            }
        }
    }
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
