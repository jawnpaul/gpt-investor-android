package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiElevation
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlin.time.TimeSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

enum class GiToastResult { Dismissed, ActionPerformed }

@Stable
class GiToastData internal constructor(
    val message: String,
    val icon: ImageVector?,
    val actionText: String?,
    internal val result: CompletableDeferred<GiToastResult>
) {
    internal var held by mutableStateOf(false)
}

@Stable
class GiToastState internal constructor(private val accessibilityManager: AccessibilityManager?) {
    var current: GiToastData? by mutableStateOf(null)
        private set

    suspend fun show(message: String, icon: ImageVector? = null, actionText: String? = null): GiToastResult {
        val data = GiToastData(message, icon, actionText, CompletableDeferred())
        current?.result?.complete(GiToastResult.Dismissed)
        current = data
        val baseDuration = if (actionText != null) ACTION_DURATION_MS else DURATION_MS
        val duration = accessibilityManager?.calculateRecommendedTimeoutMillis(
            originalTimeoutMillis = baseDuration,
            containsIcons = icon != null,
            containsText = true,
            containsControls = actionText != null
        ) ?: baseDuration
        return try {
            coroutineScope {
                val timer = launch { data.runTimer(duration) }
                data.result.await().also { timer.cancel() }
            }
        } finally {
            if (current === data) current = null
        }
    }

    private suspend fun GiToastData.runTimer(duration: Long) {
        var remaining = duration
        while (remaining > 0) {
            snapshotFlow { held }.first { !it }
            val mark = TimeSource.Monotonic.markNow()
            withTimeoutOrNull(remaining) { snapshotFlow { held }.first { it } }
            remaining -= mark.elapsedNow().inWholeMilliseconds
        }
        result.complete(GiToastResult.Dismissed)
    }

    internal fun performAction(data: GiToastData) {
        data.result.complete(GiToastResult.ActionPerformed)
    }
}

@Composable
fun rememberGiToastState(): GiToastState {
    val accessibilityManager = LocalAccessibilityManager.current
    return remember(accessibilityManager) { GiToastState(accessibilityManager) }
}

// Place it 16 above the bottom nav (or the safe area), inset by the gutter.
@Composable
fun GiToastHost(state: GiToastState, modifier: Modifier = Modifier) {
    val reducedMotion = isReducedMotionEnabled()
    val slide = with(LocalDensity.current) { 16.dp.roundToPx() }
    AnimatedContent(
        targetState = state.current,
        modifier = modifier,
        transitionSpec = {
            val enterSpec = tween<IntOffset>(GiMotion.BASE, easing = EaseOutQuart)
            val exitSpec = tween<IntOffset>(GiMotion.FAST, easing = EaseOutQuart)
            val enter: EnterTransition = fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart)) +
                if (reducedMotion) EnterTransition.None else slideInVertically(enterSpec) { slide }
            val exit: ExitTransition = fadeOut(tween(GiMotion.FAST, easing = EaseOutQuart)) +
                if (reducedMotion) ExitTransition.None else slideOutVertically(exitSpec) { slide }
            enter togetherWith exit
        },
        label = "GiToastHost"
    ) { data ->
        if (data != null) {
            GiToast(
                message = data.message,
                modifier = Modifier.pointerInput(data) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        data.held = true
                        try {
                            do {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                            } while (event.changes.any { it.pressed })
                        } finally {
                            data.held = false
                        }
                    }
                },
                icon = data.icon,
                actionText = data.actionText,
                onAction = { state.performAction(data) }
            )
        }
    }
}

@Composable
fun GiToast(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionText: String? = null,
    onAction: () -> Unit = {}
) {
    val colors = GiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(shape = GiRadius.md, shadow = GiElevation.overlay(colors.isDark))
            .heightIn(min = 48.dp)
            .clip(GiRadius.md)
            .background(colors.ink)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.md),
                tint = colors.bg
            )
        }
        GiText(
            text = message,
            modifier = Modifier.weight(1f),
            style = GiTextStyle.BodySmall,
            color = colors.bg,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (actionText != null) {
            GiText(
                text = actionText,
                modifier = Modifier
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = GiSpace.x1),
                style = GiTextStyle.Link,
                color = colors.invAction
            )
        }
    }
}

private const val DURATION_MS = 4_000L
private const val ACTION_DURATION_MS = 6_000L

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiToastPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiToast(message = "Added Apple to your watchlist", icon = GiIcons.check, actionText = "Undo")
            GiToast(message = "Price alert set for $240", icon = GiIcons.bell)
            GiToast(message = "Couldn't save. Try again.", icon = GiIcons.warningCircle, actionText = "Retry")
        }
    }
}
