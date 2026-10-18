package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.discover_ask_about
import com.thejawnpaul.gptinvestor.discover_ask_subtitle
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import org.jetbrains.compose.resources.stringResource

private val AskRowPaddingVertical = 14.dp

@Composable
fun SearchAskGptRow(query: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(GiRadius.md)
            .background(colors.violetSoft)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .background(if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .padding(horizontal = GiSpace.x4, vertical = AskRowPaddingVertical),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = GiIcons.mark,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.lg),
            tint = colors.ink
        )
        Column(modifier = Modifier.weight(1f)) {
            GiText(text = stringResource(Res.string.discover_ask_about, query), style = GiTextStyle.Label)
            GiText(text = stringResource(Res.string.discover_ask_subtitle), style = GiTextStyle.MetaRelaxed)
        }
        Icon(
            imageVector = GiIcons.caretRight,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.sm),
            tint = colors.muted
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SearchAskGptRowPreview() {
    GPTInvestorTheme {
        SearchAskGptRow(
            query = "nvi",
            onClick = {},
            modifier = Modifier.background(GiTheme.colors.bg).padding(GiSpace.gutter)
        )
    }
}
