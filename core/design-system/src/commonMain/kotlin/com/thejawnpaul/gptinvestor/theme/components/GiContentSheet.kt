package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

private val SheetCorner = 12.dp
private val SheetShape = RoundedCornerShape(topStart = SheetCorner, topEnd = SheetCorner)

@Composable
fun GiContentSheet(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = GiSpace.gutter, vertical = GiSpace.x6),
    verticalSpacing: Dp = GiSpace.x8,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = GiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SheetShape)
            .background(colors.raised)
            .drawWithCache {
                val stroke = GiEdge.width.toPx()
                val corner = SheetCorner.toPx()
                val edge = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = stroke / 2f,
                            top = stroke / 2f,
                            right = size.width - stroke / 2f,
                            bottom = size.height + corner,
                            topLeftCornerRadius = CornerRadius(corner),
                            topRightCornerRadius = CornerRadius(corner)
                        )
                    )
                }
                val edgeStroke = Stroke(width = stroke)
                onDrawWithContent {
                    drawContent()
                    clipRect(left = 0f, top = 0f, right = size.width, bottom = corner) {
                        drawPath(path = edge, color = colors.line, style = edgeStroke)
                    }
                }
            }
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        content = content
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiContentSheetPreview() {
    GiPreviewSurface {
        GiContentSheet {
            GiText(text = "Summary", style = GiTextStyle.SectionTitle)
            GiText(text = "A raised panel docked to the bottom of the page.")
        }
    }
}
