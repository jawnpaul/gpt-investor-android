package com.thejawnpaul.gptinvestor.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.round
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private const val STEP_FADE_OUT_MS = 90
private const val STEP_FADE_IN_MS = 210

@Composable
fun rememberGiSharedAxisTransition(): (forward: Boolean) -> ContentTransform {
    val reducedMotion = isReducedMotionEnabled()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val offset = with(LocalDensity.current) { GiSpace.x8.roundToPx() }
    return remember(reducedMotion, isRtl, offset) {
        { forward ->
            if (reducedMotion) {
                fadeIn(tween(GiMotion.FAST)) togetherWith fadeOut(tween(GiMotion.FAST))
            } else {
                val direction = if (forward != isRtl) 1 else -1
                slideInHorizontally(GiMotion.smooth()) { offset * direction } +
                    fadeIn(tween(STEP_FADE_IN_MS, delayMillis = STEP_FADE_OUT_MS, easing = EaseOutQuart)) togetherWith
                    slideOutHorizontally(GiMotion.smooth()) { -offset * direction } +
                    fadeOut(tween(STEP_FADE_OUT_MS, easing = LinearEasing))
            }
        }
    }
}

fun Modifier.giAnimatePlacement(): Modifier = this then AnimatePlacementElement

private data object AnimatePlacementElement : ModifierNodeElement<AnimatePlacementNode>() {
    override fun create() = AnimatePlacementNode()

    override fun update(node: AnimatePlacementNode) = Unit
}

private class AnimatePlacementNode :
    Modifier.Node(),
    LayoutModifierNode {
    private var offset: Animatable<IntOffset, AnimationVector2D>? = null

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            val target = coordinates?.positionInParent()?.round()
            if (target == null || isLookingAhead) {
                placeable.place(0, 0)
                return@layout
            }
            val animatable = offset ?: Animatable(target, IntOffset.VectorConverter).also { offset = it }
            if (animatable.targetValue != target) {
                coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                    animatable.animateTo(target, GiMotion.smooth())
                }
            }
            placeable.place(animatable.value - target)
        }
    }

    override fun onReset() {
        offset = null
    }
}
