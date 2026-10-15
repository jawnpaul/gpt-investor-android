package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiTopBar(
    title: String?,
    onBack: () -> Unit,
    backContentDescription: String,
    modifier: Modifier = Modifier,
    scrolled: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GiTheme.colors.bg)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = GiSpace.gutter, vertical = GiSpace.x2),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiIconButton(icon = GiIcons.caretLeft, contentDescription = backContentDescription, onClick = onBack)
            if (title != null) {
                GiText(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                    style = GiTextStyle.SectionTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2), content = actions)
        }
        if (scrolled) {
            GiDivider()
        }
    }
}

// Home uses GiGreetingHeader instead.
@Composable
fun GiLargeTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x2),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            GiText(text = title, modifier = Modifier.semantics { heading() }, style = GiTextStyle.ScreenTitle)
            if (subtitle != null) {
                GiText(text = subtitle, style = GiTextStyle.Meta)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2), content = actions)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiTopBarPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiLargeTopBar(title = "Watchlist", subtitle = "5 stocks · 2 up today") {
                GiIconButton(icon = GiIcons.magnifyingGlass, contentDescription = "Search", onClick = {})
            }
            GiTopBar(
                title = "What are the biggest tech stocks this year?",
                onBack = {},
                backContentDescription = "Back",
                scrolled = true
            ) {
                GiIconButton(icon = GiIcons.clock, contentDescription = "History", onClick = {})
            }
            GiTopBar(title = null, onBack = {}, backContentDescription = "Back") {
                GiIconButton(icon = GiIcons.shareNetwork, contentDescription = "Share", onClick = {})
                GiIconButton(icon = GiIcons.plus, contentDescription = "Add to watchlist", onClick = {})
            }
        }
    }
}
