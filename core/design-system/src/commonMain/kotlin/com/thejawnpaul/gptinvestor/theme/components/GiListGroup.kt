package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import com.thejawnpaul.gptinvestor.theme.GiColors
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Put GiDivider between rows.
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

fun Modifier.giLazyListGroup(state: LazyListState, contentType: Any, inset: Dp, colors: GiColors): Modifier =
    drawWithCache {
        val stroke = GiEdge.width.toPx()
        val edge = Stroke(width = stroke)
        val radius = CornerRadius(GiRadius.md.topStart.toPx(size, this))
        val insetPx = inset.toPx()
        val overhang = radius.x + stroke
        val corners = Path()
        onDrawWithContent {
            val info = state.layoutInfo
            val items = info.visibleItemsInfo
            val first = items.indexOfFirst { it.contentType == contentType }
            if (first < 0) {
                drawContent()
                return@onDrawWithContent
            }
            val last = items.indexOfLast { it.contentType == contentType }
            val origin = -info.viewportStartOffset.toFloat()
            val start = items[first]
            val end = items[last]
            val top = if (first > 0 || start.index == 0) origin + start.offset else -overhang
            val bottom = if (last < items.lastIndex || end.index == info.totalItemsCount - 1) {
                origin + end.offset + end.size
            } else {
                size.height + overhang
            }
            val card = Rect(insetPx, top, size.width - insetPx, bottom)
            val topLeft = Offset(card.left + stroke / 2f, card.top + stroke / 2f)
            val cardSize = Size(card.width - stroke, card.height - stroke)
            corners.reset()
            corners.fillType = PathFillType.EvenOdd
            corners.addRect(card)
            corners.addRoundRect(RoundRect(card, radius))
            clipRect {
                drawRoundRect(color = colors.raised, topLeft = topLeft, size = cardSize, cornerRadius = radius)
            }
            clipPath(corners, clipOp = ClipOp.Difference) { this@onDrawWithContent.drawContent() }
            clipRect {
                for (i in first until last) {
                    val y = origin + items[i].offset + items[i].size - stroke / 2f
                    drawLine(
                        color = colors.line,
                        start = Offset(card.left, y),
                        end = Offset(card.right, y),
                        strokeWidth = stroke
                    )
                }
                drawRoundRect(
                    color = colors.line,
                    topLeft = topLeft,
                    size = cardSize,
                    cornerRadius = radius,
                    style = edge
                )
            }
        }
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
    leading: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    minHeight: Dp = GiSpace.stockRow,
    trailing: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
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
            .background(if (isPressed) GiTheme.colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .heightIn(min = minHeight)
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading?.invoke()
        Column(modifier = Modifier.weight(1f)) {
            GiText(
                text = title,
                style = GiTextStyle.Label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                GiText(
                    text = subtitle,
                    style = GiTextStyle.Meta,
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
