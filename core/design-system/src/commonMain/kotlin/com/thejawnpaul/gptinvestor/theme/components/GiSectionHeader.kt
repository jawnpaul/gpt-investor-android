package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiEyebrow(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = GiTheme.colors.muted
            )
        }
        GiText(text = text, style = GiTextStyle.Eyebrow)
    }
}

@Composable
fun GiSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    eyebrowIcon: ImageVector? = null,
    trailingText: String? = null,
    onTrailingClick: (() -> Unit)? = null
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
        if (eyebrow != null) {
            GiEyebrow(text = eyebrow, icon = eyebrowIcon)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiText(
                text = title,
                modifier = Modifier.weight(1f).alignByBaseline(),
                style = GiTextStyle.SectionTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (trailingText != null) {
                GiText(
                    text = trailingText,
                    modifier = Modifier
                        .alignByBaseline()
                        .then(
                            if (onTrailingClick != null) {
                                Modifier.clickable(role = Role.Button, onClick = onTrailingClick)
                            } else {
                                Modifier
                            }
                        ),
                    style = if (onTrailingClick != null) GiTextStyle.Link else GiTextStyle.Meta,
                    maxLines = 1
                )
            }
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiSectionHeaderPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x6)) {
            GiSectionHeader(
                title = "Today's brief",
                eyebrow = "Morning brief",
                eyebrowIcon = GiIcons.calendarBlank,
                trailingText = "Tue 13 Oct"
            )
            GiSectionHeader(
                title = "Movers right now",
                eyebrow = "Trending today",
                eyebrowIcon = GiIcons.fire,
                trailingText = "See all",
                onTrailingClick = {}
            )
        }
    }
}
