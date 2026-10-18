package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.features.search.domain.model.ChipItem
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiControlHeight
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle

@Composable
fun ChipListSection(chips: List<ChipItem>, onChipClick: (String) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
    ) {
        chips.forEach { chip ->
            SearchActionChip(text = chip.label, onClick = { onChipClick(chip.key) })
        }
    }
}

@Composable
private fun SearchActionChip(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier = modifier
            .heightIn(min = GiControlHeight.chipS)
            .clip(GiRadius.xs)
            .background(colors.raised)
            .border(GiEdge.width, colors.line, GiRadius.xs)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .background(if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .padding(horizontal = GiSpace.x3, vertical = GiSpace.x1),
        contentAlignment = Alignment.Center
    ) {
        GiText(text = text, style = GiTextStyle.BodySmall, color = colors.ink, fontWeight = FontWeight.Medium)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun ChipListSectionPreview() {
    GPTInvestorTheme {
        ChipListSection(
            chips = listOf(
                ChipItem("healthcare", "Healthcare"),
                ChipItem("industrials", "Industrials"),
                ChipItem("energy", "Energy"),
                ChipItem("basic-materials", "Basic materials"),
                ChipItem("utilities", "Utilities")
            ),
            onChipClick = {},
            modifier = Modifier.background(GiTheme.colors.bg).padding(GiSpace.gutter)
        )
    }
}
