package com.thejawnpaul.gptinvestor.features.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import kotlinx.coroutines.launch

fun Modifier.shimmerEffect(colorOne: Color, colorTwo: Color): Modifier = this.then(
    ShimmerElement(colorOne, colorTwo)
)

private data class ShimmerElement(val colorOne: Color, val colorTwo: Color) : ModifierNodeElement<ShimmerNode>() {
    override fun create(): ShimmerNode = ShimmerNode(colorOne, colorTwo)

    override fun update(node: ShimmerNode) {
        node.colorOne = colorOne
        node.colorTwo = colorTwo
    }
}

private class ShimmerNode(var colorOne: Color, var colorTwo: Color) :
    Modifier.Node(),
    DrawModifierNode {
    private val progress = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000)
                )
            )
        }
    }

    override fun ContentDrawScope.draw() {
        val width = size.width
        val height = size.height

        if ((width > 0) && (height > 0)) {
            val initialValue = -2 * width
            val totalRange = 4 * width
            val startOffsetX = initialValue + progress.value * totalRange

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorOne,
                        colorTwo,
                        colorOne
                    ),
                    start = Offset(startOffsetX, 0f),
                    end = Offset(startOffsetX + width, height)
                )
            )
        }
        drawContent()
    }
}
