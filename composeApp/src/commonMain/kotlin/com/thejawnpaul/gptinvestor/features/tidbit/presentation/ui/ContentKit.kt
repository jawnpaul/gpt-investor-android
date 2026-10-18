package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.thejawnpaul.gptinvestor.theme.GiControlHeight
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

private const val LARGE_TEXT_FONT_SCALE = 1.5f

@Composable
@ReadOnlyComposable
internal fun isContentLargeText(): Boolean = LocalDensity.current.fontScale >= LARGE_TEXT_FONT_SCALE

internal val ContentBookmarkFill: ImageVector get() = Icons.Filled.Bookmark

@Composable
internal fun articleBodyStyle(): TextStyle =
    GiTheme.type.input.copy(lineHeight = ARTICLE_LINE_HEIGHT.em, color = GiTheme.colors.body)

private const val ARTICLE_LINE_HEIGHT = 1.625f

internal fun Modifier.overlapMargin(start: Dp = 0.dp, top: Dp = 0.dp, end: Dp = 0.dp, bottom: Dp = 0.dp): Modifier =
    layout { measurable, constraints ->
        val startPx = start.roundToPx()
        val endPx = end.roundToPx()
        val topPx = top.roundToPx()
        val bottomPx = bottom.roundToPx()
        val placeable = measurable.measure(constraints)
        val width = (placeable.width - startPx - endPx).coerceAtLeast(0)
        val height = (placeable.height - topPx - bottomPx).coerceAtLeast(0)
        layout(width, height) { placeable.place(-startPx, -topPx) }
    }

@Composable
internal fun ContentTag(
    text: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    compact: Boolean = false
) {
    Row(
        modifier = modifier
            .heightIn(min = if (compact) GiSpace.x5 else GiControlHeight.pill)
            .clip(GiRadius.xs)
            .background(container)
            .padding(horizontal = if (compact) GiSpace.x2 else GiSpace.x2_5),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(GiIconSize.sm), tint = content)
        }
        GiText(
            text = text,
            style = if (compact) GiTextStyle.TileInitials else GiTextStyle.Tag,
            color = content,
            maxLines = 1
        )
    }
}

@Composable
internal fun LazyItemScope.contentItemMotion(): Modifier = if (isReducedMotionEnabled()) {
    Modifier.animateItem(fadeInSpec = null, placementSpec = null, fadeOutSpec = null)
} else {
    Modifier.animateItem(
        fadeInSpec = null,
        placementSpec = GiMotion.smooth(),
        fadeOutSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised)
    )
}

@Composable
internal fun ContentEmptyState(
    icon: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    val colors = GiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = EMPTY_TOP_PADDING),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x5),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = EMPTY_TILE_WIDTH, height = EMPTY_TILE_HEIGHT)
                .clip(GiRadius.md)
                .background(colors.violetSoft)
                .clearAndSetSemantics { }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .offset(x = EMPTY_ICON_X, y = EMPTY_ICON_Y)
                    .size(EMPTY_ICON_SIZE),
                tint = colors.violet
            )
            Box(
                modifier = Modifier
                    .offset(x = GiSpace.x6, y = EMPTY_BAR_1_Y)
                    .size(width = EMPTY_BAR_1_WIDTH, height = GiSpace.x2)
                    .alpha(EMPTY_BAR_1_ALPHA)
                    .clip(GiRadius.sm)
                    .background(colors.lilac)
            )
            Box(
                modifier = Modifier
                    .offset(x = EMPTY_BAR_2_X, y = EMPTY_BAR_2_Y)
                    .size(width = EMPTY_BAR_2_WIDTH, height = GiSpace.x2)
                    .alpha(EMPTY_BAR_2_ALPHA)
                    .clip(GiRadius.sm)
                    .background(colors.lilac)
            )
        }
        Column(
            modifier = Modifier.widthIn(max = EMPTY_TEXT_MAX_WIDTH),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GiText(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = GiTextStyle.PageTitle,
                textAlign = TextAlign.Center
            )
            GiText(text = body, style = GiTextStyle.Body, textAlign = TextAlign.Center)
        }
        if (actionText != null && onAction != null) {
            GiButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
                size = GiButtonSize.Large
            )
        }
    }
}

private val WHITESPACE = Regex("\\s+")

internal fun readMinutes(content: String): Int {
    val words = content.split(WHITESPACE).count { it.isNotBlank() }
    return ((words + WORDS_PER_MINUTE - 1) / WORDS_PER_MINUTE).coerceAtLeast(1)
}

private const val WORDS_PER_MINUTE = 200

private val EMPTY_TOP_PADDING = 72.dp
private val EMPTY_TILE_WIDTH = 160.dp
private val EMPTY_TILE_HEIGHT = 140.dp
private val EMPTY_ICON_SIZE = 56.dp
private val EMPTY_ICON_X = 52.dp
private val EMPTY_ICON_Y = 26.dp
private val EMPTY_BAR_1_Y = 96.dp
private val EMPTY_BAR_1_WIDTH = 112.dp
private const val EMPTY_BAR_1_ALPHA = 0.6f
private val EMPTY_BAR_2_X = 44.dp
private val EMPTY_BAR_2_Y = 112.dp
private val EMPTY_BAR_2_WIDTH = 72.dp
private const val EMPTY_BAR_2_ALPHA = 0.35f
private val EMPTY_TEXT_MAX_WIDTH = 300.dp
