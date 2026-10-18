package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// The icon tile stands in for the 3D art that comes later.
@Composable
fun GiEmptyState(
    icon: ImageVector,
    title: String,
    body: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 104.dp)
                .clip(GiRadius.md)
                .background(GiTheme.colors.violetSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.xl),
                tint = GiTheme.colors.violet
            )
        }
        GiText(text = title, style = GiTextStyle.SectionTitle, textAlign = TextAlign.Center)
        GiText(text = body, style = GiTextStyle.BodySmall, textAlign = TextAlign.Center)
        GiButton(text = actionText, onClick = onAction, size = GiButtonSize.Medium)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiEmptyStatePreview() {
    GiPreviewSurface {
        GiEmptyState(
            icon = GiIcons.star,
            title = "Start your watchlist",
            body = "Add stocks you care about and we'll tell you when they move.",
            actionText = "Find stocks",
            onAction = {}
        )
    }
}
