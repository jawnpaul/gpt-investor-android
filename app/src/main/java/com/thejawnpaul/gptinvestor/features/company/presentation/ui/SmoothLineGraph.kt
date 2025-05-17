package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import android.graphics.PointF
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.unit.toSize
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
fun SmoothLineGraph(data: List<GraphPoint>) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxSize()
    ) {
        val animationProgress = remember { Animatable(0f) }
        var highlightedWeek by remember { mutableStateOf<Int?>(null) }
        val localView = LocalView.current

        LaunchedEffect(highlightedWeek) {
            if (highlightedWeek != null) {
                localView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        }

        LaunchedEffect(key1 = data, block = {
            animationProgress.animateTo(1f, tween(3000))
        })

        val coroutineScope = rememberCoroutineScope()
        val textMeasurer = rememberTextMeasurer()
        val labelTextStyle = MaterialTheme.typography.labelSmall

        Spacer(
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(3 / 2f)
                .fillMaxSize()
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures {
                        coroutineScope.launch {
                            animationProgress.snapTo(0f)
                            animationProgress.animateTo(1f, tween(3000))
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { offset ->
                            highlightedWeek =
                                (offset.x / (size.width / (data.size - 1))).roundToInt()
                        },
                        onDragEnd = { highlightedWeek = null },
                        onDragCancel = { highlightedWeek = null },
                        onDrag = { change, _ ->
                            highlightedWeek =
                                (change.position.x / (size.width / (data.size - 1))).roundToInt()
                        }
                    )
                }
                .drawWithCache {
                    val path = generateSmoothPath(data, size)
                    val filledPath = Path()
                    filledPath.addPath(path)
                    filledPath.relativeLineTo(0f, size.height)
                    filledPath.lineTo(0f, size.height)
                    filledPath.close()

                    onDrawBehind {
                        val barWidthPx = 1.dp.toPx()

                        val horizontalLines = 3
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        val sectionSize = size.height / (horizontalLines + 1)
                        repeat(horizontalLines) { i ->
                            val startY = sectionSize * (i + 1)
                            drawLine(
                                color = gptInvestorColors.utilColors.borderBright10,
                                start = Offset(0f, startY),
                                end = Offset(size.width, startY),
                                strokeWidth = barWidthPx,
                                pathEffect = pathEffect
                            )
                        }

                        // draw line
                        clipRect(right = size.width * animationProgress.value) {
                            drawPath(
                                path,
                                gptInvestorColors.greenColors.allGreen,
                                style = Stroke(2.dp.toPx())
                            )

                            drawPath(
                                filledPath,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        gptInvestorColors.greenColors.allGreen.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                ),
                                style = Fill
                            )
                        }

                        // draw highlight if user is dragging
                        highlightedWeek?.let {
                            this.drawHighlight(
                                it.coerceIn(0, data.lastIndex),
                                data,
                                textMeasurer,
                                labelTextStyle
                            )
                        }
                    }
                }
        )
    }
}

fun generateSmoothPath(data: List<GraphPoint>, size: Size): Path {
    val path = Path()
    val numberEntries = data.size - 1
    val weekWidth = size.width / numberEntries

    val max = data.maxBy { it.amount }
    val min = data.minBy { it.amount } // will map to x= 0, y = height
    val range = max.amount - min.amount
    val heightPxPerAmount = size.height / range

    var previousPointX = 0f
    var previousPointY = size.height
    data.forEachIndexed { i, balance ->
        if (i == 0) {
            path.moveTo(
                0f,
                size.height - (balance.amount - min.amount) *
                    heightPxPerAmount
            )
        }

        val balanceX = i * weekWidth
        val balanceY = size.height - (balance.amount - min.amount) *
            heightPxPerAmount
        // to do smooth curve graph - we use cubicTo, uncomment section below for non-curve
        val controlPoint1 = PointF((balanceX + previousPointX) / 2f, previousPointY)
        val controlPoint2 = PointF((balanceX + previousPointX) / 2f, balanceY)
        path.cubicTo(
            controlPoint1.x,
            controlPoint1.y,
            controlPoint2.x,
            controlPoint2.y,
            balanceX,
            balanceY
        )

        previousPointX = balanceX
        previousPointY = balanceY
    }
    return path
}

fun DrawScope.drawHighlight(highlightedWeek: Int, graphData: List<GraphPoint>, textMeasurer: TextMeasurer, labelTextStyle: TextStyle) {
    val amount = graphData[highlightedWeek].amount
    val date = graphData[highlightedWeek].date
    val minAmount = graphData.minBy { it.amount }.amount
    val range = graphData.maxBy { it.amount }.amount - minAmount
    val percentageHeight = ((amount - minAmount) / range)
    val pointY = size.height - (size.height * percentageHeight)
    // draw vertical line on week
    val x = highlightedWeek * (size.width / (graphData.size - 1))
    drawLine(
        HighlightColor,
        start = Offset(x, 0f),
        end = Offset(x, size.height),
        strokeWidth = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
    )

    // draw hit circle on graph
    drawCircle(
        Color(0xFF02A400),
        radius = 4.dp.toPx(),
        center = Offset(x, pointY)
    )

    // draw info box
    val textLayoutResult = textMeasurer.measure(
        text = buildString {
            append("$$amount")
            append("\n")
            append(date)
        },
        style = labelTextStyle
    )
    val highlightContainerSize = (textLayoutResult.size).toIntRect().inflate(4.dp.roundToPx()).size
    val boxTopLeft = (x - (highlightContainerSize.width / 2f))
        .coerceIn(0f, size.width - highlightContainerSize.width)
    drawRoundRect(
        Color.White,
        topLeft = Offset(boxTopLeft, 0f),
        size = highlightContainerSize.toSize(),
        cornerRadius = CornerRadius(8.dp.toPx())
    )
    drawText(
        textLayoutResult,
        color = Color.Black,
        topLeft = Offset(boxTopLeft + 4.dp.toPx(), 4.dp.toPx())
    )
}

data class GraphPoint(val date: String, val amount: Float)

val HighlightColor = Color.White.copy(alpha = 0.7f)

/** Many thanks to
https://github.com/riggaroo/compose-playtime/blob/main/app/src/main/java/dev/riggaroo/composeplaytime/SmoothLineGraph.kt
 **/
