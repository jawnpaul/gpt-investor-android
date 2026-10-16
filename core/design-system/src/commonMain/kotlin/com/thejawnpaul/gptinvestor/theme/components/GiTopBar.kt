package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiTopBarTitleStyle { Start, Centered, Page }

@Composable
fun GiTopBar(
    title: String?,
    onBack: (() -> Unit)?,
    backContentDescription: String,
    modifier: Modifier = Modifier,
    scrolled: Boolean = false,
    titleStyle: GiTopBarTitleStyle = GiTopBarTitleStyle.Start,
    subtitle: String? = null,
    center: (@Composable RowScope.() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val centered = titleStyle == GiTopBarTitleStyle.Centered
    val sideSlot = if (centered) Modifier.widthIn(min = GiSpace.touchTarget) else Modifier
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GiTheme.colors.bg)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier.padding(
                start = GiSpace.gutter,
                end = GiSpace.gutter,
                top = GiSpace.x2,
                bottom = GiSpace.x3
            ),
            horizontalArrangement = Arrangement.spacedBy(if (center != null) GiSpace.x4 else GiSpace.x3),
            verticalAlignment = if (subtitle != null) Alignment.Top else Alignment.CenterVertically
        ) {
            if (onBack != null) {
                GiIconButton(
                    icon = GiIcons.caretLeft,
                    contentDescription = backContentDescription,
                    onClick = onBack,
                    modifier = sideSlot
                )
            } else if (centered) {
                Spacer(modifier = sideSlot)
            }
            when {
                center != null -> Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    content = center
                )
                else -> AnimatedContent(
                    targetState = title,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart)) togetherWith
                            fadeOut(tween(GiMotion.FAST, easing = EaseOutQuart))
                    }
                ) { currentTitle ->
                    if (currentTitle != null) {
                        GiTopBarTitle(
                            title = currentTitle,
                            subtitle = subtitle,
                            titleStyle = titleStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Spacer(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            Row(
                modifier = sideSlot,
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x2, Alignment.End),
                content = actions
            )
        }
        if (scrolled) {
            GiDivider()
        }
    }
}

@Composable
private fun GiTopBarTitle(
    title: String,
    subtitle: String?,
    titleStyle: GiTopBarTitleStyle,
    modifier: Modifier = Modifier
) {
    val maxLines = when {
        titleStyle == GiTopBarTitleStyle.Page -> Int.MAX_VALUE
        isLargeText() -> 2
        else -> 1
    }
    val textAlign = if (titleStyle == GiTopBarTitleStyle.Centered) TextAlign.Center else TextAlign.Start
    Column(
        modifier = modifier.then(if (subtitle != null) Modifier.padding(top = GiSpace.x2_5) else Modifier),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x0_5)
    ) {
        GiText(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { heading() },
            style = if (titleStyle == GiTopBarTitleStyle.Page) GiTextStyle.PageTitle else GiTextStyle.SectionTitle,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        if (subtitle != null) {
            GiText(
                text = subtitle,
                modifier = Modifier.fillMaxWidth(),
                style = GiTextStyle.MetaRelaxed,
                color = GiTheme.colors.muted,
                textAlign = textAlign
            )
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
                GiIconButton(icon = GiIcons.clockCounterClockwise, contentDescription = "History", onClick = {})
            }
            GiTopBar(title = null, onBack = {}, backContentDescription = "Back") {
                GiIconButton(icon = GiIcons.shareNetwork, contentDescription = "Share", onClick = {})
                GiIconButton(icon = GiIcons.plus, contentDescription = "Add to watchlist", onClick = {})
            }
            GiTopBar(
                title = "TSLA",
                onBack = {},
                backContentDescription = "Back",
                titleStyle = GiTopBarTitleStyle.Centered
            )
            GiTopBar(title = "Saved picks", onBack = {}, backContentDescription = "Back", subtitle = "12 picks")
            GiTopBar(
                title = "Create your account",
                onBack = {},
                backContentDescription = "Back",
                titleStyle = GiTopBarTitleStyle.Page
            )
            GiTopBar(title = null, onBack = {}, backContentDescription = "Back", center = {
                GiStepDots(count = 2, current = 0)
            }) {
                GiButton(text = "Skip", onClick = {}, size = GiButtonSize.Medium, variant = GiButtonVariant.TextMuted)
            }
            GiTopBar(title = "Welcome", onBack = null, backContentDescription = "Back")
        }
    }
}
