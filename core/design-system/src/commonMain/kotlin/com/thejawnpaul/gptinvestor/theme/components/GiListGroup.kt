package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Rows stacked in one raised, hairline-edged group; put GiDivider between rows.
@Composable
fun GiListGroup(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(GiRadius.md)
            .background(GiTheme.colors.raised)
            .border(GiEdge.width, GiTheme.colors.line, GiRadius.md),
        content = content
    )
}

@Composable
fun GiDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, thickness = GiEdge.width, color = GiTheme.colors.line)
}

@Composable
fun GiListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = GiTheme.type.label,
                color = GiTheme.colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = GiTheme.type.meta,
                    color = GiTheme.colors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        trailing?.invoke()
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiListGroupPreview() {
    GiPreviewSurface {
        GiListGroup {
            GiListRow(title = "AAPL", subtitle = "Apple Inc.") {
                GiButton(text = "Add", onClick = {}, size = GiButtonSize.Small, variant = GiButtonVariant.Secondary)
            }
            GiDivider()
            GiListRow(title = "NVDA", subtitle = "NVIDIA Corporation") {
                GiSpinner()
            }
        }
    }
}
