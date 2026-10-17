package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

@Composable
fun GiStockRow(
    stock: GiStock,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    price: String? = null,
    showCaret: Boolean = false,
    locked: Boolean = false,
    lockedContentDescription: String? = null,
    status: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressedAlpha = pressedLayerAlpha(interactionSource)
    val description = listOfNotNull(
        stock.title,
        lockedContentDescription.takeIf { locked },
        stock.subtitle,
        price,
        stock.percent?.let { deltaDescription(it) }
    ).joinToString(", ")
    val isLargeText = isLargeText()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .semantics(mergeDescendants = true) { contentDescription = description }
            .background(if (pressedAlpha > 0f) GiTheme.colors.ink.copy(alpha = pressedAlpha) else Color.Transparent)
            .heightIn(min = GiSpace.stockRow)
            .padding(
                start = GiSpace.x4,
                end = if (trailing != null) GiSpace.x1 else GiSpace.x4,
                top = GiSpace.x3,
                bottom = GiSpace.x3
            ),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = if (isLargeText) Alignment.Top else Alignment.CenterVertically
    ) {
        GiLogoTile(initials = stock.initials, brandColor = stock.brandColor, imageUrl = stock.imageUrl)
        if (isLargeText) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiStockRowName(stock = stock, locked = locked, wrap = true, status = status)
                if (price != null || stock.percent != null) {
                    FlowRow(
                        modifier = Modifier.clearAndSetSemantics {},
                        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                        verticalArrangement = Arrangement.spacedBy(GiSpace.x1),
                        itemVerticalAlignment = Alignment.CenterVertically
                    ) {
                        if (price != null) GiStockRowPrice(price)
                        if (stock.percent != null) GiDeltaPill(percent = stock.percent)
                    }
                }
            }
        } else {
            GiStockRowName(
                stock = stock,
                locked = locked,
                wrap = false,
                status = status,
                modifier = Modifier.weight(1f)
            )
            if (price != null) {
                Column(
                    modifier = Modifier.clearAndSetSemantics {},
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    GiStockRowPrice(price)
                    if (stock.percent != null) GiDeltaPill(percent = stock.percent)
                }
            } else if (stock.percent != null) {
                GiDeltaPill(percent = stock.percent)
            }
        }
        GiStockRowEnd(trailing = trailing, showCaret = showCaret)
    }
}

@Composable
private fun GiStockRowName(
    stock: GiStock,
    locked: Boolean,
    wrap: Boolean,
    status: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    val maxLines = if (wrap) Int.MAX_VALUE else 1
    Column(modifier = modifier) {
        if (wrap && locked) {
            GiStockRowWrappedLockedTitle(title = stock.title)
        } else {
            GiStockRowTitle(title = stock.title, locked = locked, maxLines = maxLines)
        }
        GiText(
            text = stock.subtitle,
            modifier = Modifier.clearAndSetSemantics {},
            style = GiTextStyle.Meta,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        if (status != null) {
            Row(modifier = Modifier.padding(top = 2.dp)) { status() }
        }
    }
}

@Composable
private fun GiStockRowTitle(title: String, locked: Boolean, maxLines: Int) {
    Row(
        modifier = Modifier.clearAndSetSemantics {},
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiText(
            text = title,
            modifier = Modifier.weight(1f, fill = false),
            style = GiTextStyle.Label,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        if (locked) GiStockRowLock()
    }
}

@Composable
private fun GiStockRowWrappedLockedTitle(title: String) {
    val lockSize = with(LocalDensity.current) { LOCK_SIZE.toSp() }
    Text(
        text = buildAnnotatedString {
            append(title)
            append(" ")
            appendInlineContent(LOCK_ID)
        },
        modifier = Modifier.clearAndSetSemantics {},
        color = GiTheme.colors.ink,
        style = GiTextStyle.Label.textStyle(),
        inlineContent = mapOf(
            LOCK_ID to InlineTextContent(Placeholder(lockSize, lockSize, PlaceholderVerticalAlign.TextCenter)) {
                GiStockRowLock()
            }
        )
    )
}

@Composable
private fun GiStockRowLock() {
    Icon(
        imageVector = GiIcons.lock,
        contentDescription = null,
        modifier = Modifier.size(LOCK_SIZE),
        tint = GiTheme.colors.muted
    )
}

private const val LOCK_ID = "lock"
private val LOCK_SIZE = 16.dp

@Composable
private fun GiStockRowPrice(price: String) {
    val style = GiTheme.type.labelRegular.copy(fontFeatureSettings = "tnum")
    val color = GiTheme.colors.ink
    if (isReducedMotionEnabled()) {
        Text(text = price, color = color, style = style, maxLines = 1)
    } else {
        Crossfade(
            targetState = price,
            animationSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised),
            label = "rowPrice"
        ) { shown ->
            Text(text = shown, color = color, style = style, maxLines = 1)
        }
    }
}

@Composable
private fun RowScope.GiStockRowEnd(trailing: (@Composable () -> Unit)?, showCaret: Boolean) {
    if (trailing != null) {
        Row(modifier = Modifier.align(Alignment.Top)) { trailing() }
    }
    if (showCaret) {
        Icon(
            imageVector = GiIcons.caretRight,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.sm),
            tint = GiTheme.colors.muted
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiStockRowPreview() {
    val apple = previewStock.copy(initials = "AA", title = "Apple", subtitle = "AAPL · NASDAQ", percent = 1.24)
    GiPreviewSurface {
        GiListGroup {
            GiStockRow(stock = apple.copy(percent = null), onClick = {})
            GiDivider()
            GiStockRow(stock = apple.copy(percent = null), onClick = {}, showCaret = true)
            GiDivider()
            GiStockRow(
                stock = previewStock.copy(title = "NVIDIA", subtitle = "NVDA · Positive", percent = 1.0),
                onClick = {},
                locked = true,
                lockedContentDescription = "Locked"
            )
            GiDivider()
            GiStockRow(stock = apple, onClick = {}, price = "$227.48")
            GiDivider()
            GiStockRow(
                stock = apple.copy(percent = -0.42),
                onClick = {},
                price = "$227.48",
                status = { GiText(text = "Alert at $230.00", style = GiTextStyle.Meta, color = GiTheme.colors.violet) },
                trailing = {
                    GiIconButton(
                        icon = GiIcons.trash,
                        contentDescription = "Remove Apple",
                        onClick = {},
                        variant = GiIconButtonVariant.Ghost
                    )
                }
            )
        }
    }
}
