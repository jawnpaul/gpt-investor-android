package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle

internal val SearchArtWidth = 160.dp
internal val SearchArtHeight = 140.dp
internal val SearchCopyMaxWidth = 300.dp

@Composable
fun SearchEmptyStateSection(message: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        SearchArtTile(
            icon = GiIcons.magnifyingGlass,
            container = GiTheme.colors.violetSoft,
            content = GiTheme.colors.violet
        )
        GiText(
            text = message,
            modifier = Modifier
                .padding(top = GiSpace.x2)
                .semantics { heading() },
            style = GiTextStyle.PageTitle,
            textAlign = TextAlign.Center
        )
        if (subtitle.isNotBlank()) {
            GiText(
                text = subtitle,
                modifier = Modifier.widthIn(max = SearchCopyMaxWidth),
                style = GiTextStyle.Body,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun SearchArtTile(icon: ImageVector, container: Color, content: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = SearchArtWidth, height = SearchArtHeight)
            .clip(GiRadius.md)
            .background(container)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(GiIconSize.xl), tint = content)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SearchEmptyStateSectionPreview() {
    GPTInvestorTheme {
        SearchEmptyStateSection(
            message = "No matches for “zzzzqq”",
            subtitle = "Check the spelling, or ask GPT Investor in plain English.",
            modifier = Modifier.background(GiTheme.colors.bg).padding(GiSpace.gutter)
        )
    }
}
