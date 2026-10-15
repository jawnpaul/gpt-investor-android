package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Android shell only: iOS keeps its native tab bar.
@Composable
fun GiBottomNav(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    val lineColor = GiTheme.colors.line
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GiTheme.colors.raised)
            .drawBehind {
                drawLine(
                    color = lineColor,
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                    strokeWidth = GiEdge.width.toPx()
                )
            }
            .navigationBarsPadding()
            .padding(horizontal = GiSpace.x3, vertical = GiSpace.x2),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun RowScope.GiBottomNavItem(
    label: String,
    icon: ImageVector,
    selectedIcon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) GiTheme.colors.violet else GiTheme.colors.muted
    val showLabel = !isLargeText()
    Column(
        modifier = modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .selectable(
                selected = selected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick
            ),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x1, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else icon,
            contentDescription = if (showLabel) null else label,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        if (showLabel) {
            GiText(
                text = label,
                style = GiTextStyle.NavLabel,
                color = color,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiBottomNavPreview() {
    GiPreviewSurface {
        GiBottomNav {
            GiBottomNavItem("Home", GiIcons.house, GiIcons.houseFill, selected = true, onClick = {})
            GiBottomNavItem("Discover", GiIcons.compass, GiIcons.compassFill, selected = false, onClick = {})
            GiBottomNavItem("Watchlist", GiIcons.star, GiIcons.starFill, selected = false, onClick = {})
            GiBottomNavItem("Profile", GiIcons.user, GiIcons.userFill, selected = false, onClick = {})
        }
    }
}
