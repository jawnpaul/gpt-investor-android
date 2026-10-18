package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.features.search.domain.model.PromptItem
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiIconTile
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileSize
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle

@Composable
fun SearchPromptRow(prompt: PromptItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = GiSpace.x4, vertical = GiSpace.x3)
    ) {
        Row(
            modifier = Modifier.heightIn(min = GiSpace.x8),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiIconTile(icon = Icons.Outlined.Sms, size = GiIconTileSize.Small)
            GiText(text = prompt.label, modifier = Modifier.weight(1f), style = GiTextStyle.LabelRegular)
            Icon(
                imageVector = GiIcons.arrowUp,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.sm).rotate(45f),
                tint = GiTheme.colors.muted
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SearchPromptRowPreview() {
    GPTInvestorTheme {
        SearchPromptRow(
            prompt = PromptItem("What makes a stock go up?", "What makes a stock go up?"),
            onClick = {},
            modifier = Modifier.background(GiTheme.colors.bg).padding(GiSpace.gutter)
        )
    }
}
