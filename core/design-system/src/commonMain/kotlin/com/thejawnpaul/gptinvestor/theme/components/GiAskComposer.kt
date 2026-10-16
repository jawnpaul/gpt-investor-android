package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiElevation
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

class GiComposerStop(val contentDescription: String, val onStop: () -> Unit)

// Keeps the line hairline on purpose (not the control edge); turns violet while focused or filled.
// Pass stop while the assistant is answering: Stop replaces Send and the beam runs around the edge.
@Composable
fun GiAskComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    placeholder: String,
    sendContentDescription: String,
    modifier: Modifier = Modifier,
    floating: Boolean = true,
    stop: GiComposerStop? = null,
    largeTextMaxLines: Int = LARGE_TEXT_MAX_LINES,
    onFocusChange: (Boolean) -> Unit = {}
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val currentOnFocusChange by rememberUpdatedState(onFocusChange)
    LaunchedEffect(isFocused) { currentOnFocusChange(isFocused) }
    val canSend = value.isNotBlank() && stop == null
    val edgeWidth = if (isFocused && value.isNotEmpty() && stop == null) GiEdge.focusWidth else GiEdge.width
    val reducedMotion = isReducedMotionEnabled()
    val lift = GiElevation.lift(colors.isDark)
    val shadowAlpha = animateFloatAsState(
        targetValue = if (floating) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(GiMotion.BASE, easing = EaseOutQuart)
    )
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = stop == null,
        textStyle = GiTheme.type.body.copy(color = colors.ink),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Send
        ),
        keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
        maxLines = if (isLargeText()) largeTextMaxLines else MAX_LINES,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(colors.violet),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .dropShadow(shape = GiRadius.md) {
                        radius = lift.radius.toPx()
                        color = lift.color
                        offset = Offset(lift.offset.x.toPx(), lift.offset.y.toPx())
                        alpha = shadowAlpha.value
                    }
                    .heightIn(min = 52.dp)
                    .giBeam(active = stop != null, shape = GiRadius.md)
                    .clip(GiRadius.md)
                    .background(colors.raised)
                    .border(
                        width = edgeWidth,
                        color = if (isFocused || value.isNotEmpty()) colors.violet else colors.line,
                        shape = GiRadius.md
                    )
                    .padding(start = if (floating) 14.dp else 16.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(if (floating) 10.dp else GiSpace.x2),
                verticalAlignment = Alignment.Bottom
            ) {
                // Padding centres the mark and each text line on the 40 button, so one line still sits mid-field.
                Icon(
                    imageVector = GiIcons.mark,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .size(20.dp),
                    tint = colors.violet
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = GiSpace.x2)
                ) {
                    if (value.isEmpty()) {
                        GiText(
                            text = placeholder,
                            style = GiTextStyle.Body,
                            color = colors.muted,
                            maxLines = if (isLargeText()) largeTextMaxLines else 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
                AnimatedContent(
                    targetState = stop,
                    transitionSpec = {
                        val enter = fadeIn(tween(GiMotion.FAST, easing = EaseOutQuart))
                        (
                            if (reducedMotion) {
                                enter
                            } else {
                                enter + scaleIn(tween(GiMotion.FAST, easing = EaseOutQuart), initialScale = SWAP_SCALE)
                            }
                            ) togetherWith fadeOut(tween(GiMotion.INSTANT))
                    },
                    contentKey = { it != null }
                ) { currentStop ->
                    if (currentStop != null) {
                        Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(GiRadius.sm)
                            .background(colors.ink)
                            .clickable(role = Role.Button, onClick = currentStop.onStop)
                            .semantics { contentDescription = currentStop.contentDescription },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = GiIcons.stopFill,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = colors.bg
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .alpha(if (canSend) 1f else GiMotion.DISABLED_ALPHA)
                            .clip(GiRadius.sm)
                            .background(colors.primary)
                            .clickable(enabled = canSend, role = Role.Button, onClick = onSend)
                            .semantics { contentDescription = sendContentDescription },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = GiIcons.arrowUp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = colors.onPrimary
                        )
                    }
                }
                }
            }
        }
    )
}

private const val MAX_LINES = 4
private const val SWAP_SCALE = 0.8f
private const val LARGE_TEXT_MAX_LINES = 2

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiAskComposerPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiAskComposer(
                value = "",
                onValueChange = {},
                onSend = {},
                placeholder = "Ask me anything about markets…",
                sendContentDescription = "Send"
            )
            GiAskComposer(
                value = "Why is NVDA up today?",
                onValueChange = {},
                onSend = {},
                placeholder = "Ask me anything about markets…",
                sendContentDescription = "Send"
            )
            GiAskComposer(
                value = "Compare Apple and Microsoft on revenue growth, margins and buybacks over the last three " +
                    "years, then tell me which looks steadier for a long-term holder and why.",
                onValueChange = {},
                onSend = {},
                placeholder = "Ask me anything about markets…",
                sendContentDescription = "Send"
            )
            GiAskComposer(
                value = "",
                onValueChange = {},
                onSend = {},
                placeholder = "Ask me anything about markets…",
                sendContentDescription = "Send",
                floating = false,
                stop = GiComposerStop(contentDescription = "Stop answering", onStop = {})
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiAskComposerDockedPreview() {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiComposerBar(docked = true) {
                GiAskComposer(
                    value = "Is JNJ a good dividend stock?",
                    onValueChange = {},
                    onSend = {},
                    placeholder = "Ask about Johnson & Johnson…",
                    sendContentDescription = "Send",
                    modifier = Modifier.focusRequester(focusRequester),
                    floating = false
                )
            }
            GiComposerBar(docked = true) {
                GiAskComposer(
                    value = "",
                    onValueChange = {},
                    onSend = {},
                    placeholder = "Ask about Johnson & Johnson…",
                    sendContentDescription = "Send",
                    floating = false,
                    stop = GiComposerStop(contentDescription = "Stop answering", onStop = {})
                )
            }
        }
    }
}
