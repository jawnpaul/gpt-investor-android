package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.FILTER_GOALS
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.filterLabelRes
import com.thejawnpaul.gptinvestor.goal_banner_subtitle
import com.thejawnpaul.gptinvestor.goal_banner_title
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
import org.jetbrains.compose.resources.stringResource

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
