package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

// The user's question: a violet-soft bubble on the right, at most 80% wide, tail corner bottom-right.
@Composable
fun GiUserMessage(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Box(modifier = Modifier.fillMaxWidth(USER_BUBBLE_MAX_WIDTH), contentAlignment = Alignment.CenterEnd) {
            GiText(
                text = text,
                modifier = Modifier
                    .clip(USER_BUBBLE_SHAPE)
                    .background(GiTheme.colors.violetSoft)
                    .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
                style = GiTextStyle.Body
            )
        }
    }
}

// The assistant's answer: no bubble, the mark and name above reading text. While streaming, a violet block
// cursor follows the text and the actions stay hidden; screen readers hear the answer once it's done.
// Answers lead with the verdict and end with "Not financial advice."
@Composable
fun GiAssistantMessage(
    name: String,
    text: String,
    modifier: Modifier = Modifier,
    streaming: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = GiIcons.mark, contentDescription = null, modifier = Modifier.size(20.dp), tint = GiTheme.colors.violet)
            GiText(text = name, style = GiTextStyle.Link, color = GiTheme.colors.muted)
        }
        if (streaming) {
            GiStreamingText(text = text)
        } else {
            GiText(
                text = text,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = GiTextStyle.Body
            )
            Row(modifier = Modifier.offset(x = -ACTIONS_INSET), content = actions)
        }
    }
}

@Composable
private fun GiStreamingText(text: String) {
    val cursorColor = GiTheme.colors.violet
    val content = remember(text) {
        buildAnnotatedString {
            append(text)
            appendInlineContent(CURSOR_ID, "")
        }
    }
    val inlineContent = remember(cursorColor) {
        mapOf(
            CURSOR_ID to InlineTextContent(Placeholder(10.sp, 16.sp, PlaceholderVerticalAlign.TextCenter)) {
                Box(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(2.dp))
                        .background(cursorColor)
                )
            }
        )
    }
    Text(
        text = content,
        inlineContent = inlineContent,
        style = GiTextStyle.Body.textStyle(),
        color = GiTheme.colors.body
    )
}

// While the assistant works: the glowing mark and a short status ("Reading the news…").
@Composable
fun GiThinkingIndicator(status: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiGlowingMark()
        GiText(text = status, style = GiTextStyle.BodySmall, color = GiTheme.colors.muted)
    }
}

@Composable
private fun GiGlowingMark() {
    val violet = GiTheme.colors.violet
    val reducedMotion = isReducedMotionEnabled()
    val transition = rememberInfiniteTransition()
    val breath by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = GLOW_PERIOD_MS / 2, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    val scale = 1f + GLOW_SCALE_DELTA * breath
                    scaleX = scale
                    scaleY = scale
                }
                .alpha(GLOW_MIN_ALPHA + (1f - GLOW_MIN_ALPHA) * breath)
                .background(Brush.radialGradient(listOf(violet.copy(alpha = GLOW_ALPHA), Color.Transparent)))
        )
        Icon(imageVector = GiIcons.mark, contentDescription = null, modifier = Modifier.size(24.dp), tint = violet)
    }
}

// An answer that failed part-way: say so, and offer Try again.
@Composable
fun GiAnswerError(message: String, retryText: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = GiIcons.warningCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.error)
            GiText(text = message, style = GiTextStyle.BodySmall, color = colors.error)
        }
        GiButton(
            text = retryText,
            onClick = onRetry,
            size = GiButtonSize.Small,
            variant = GiButtonVariant.Secondary,
            leadingIcon = GiIcons.arrowClockwise
        )
    }
}

private const val USER_BUBBLE_MAX_WIDTH = 0.8f
private val USER_BUBBLE_SHAPE = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 4.dp, bottomStart = 12.dp)
private val ACTIONS_INSET = 12.dp
private const val CURSOR_ID = "cursor"
private const val GLOW_PERIOD_MS = 3_200
private const val GLOW_ALPHA = 0.28f
private const val GLOW_MIN_ALPHA = 0.85f
private const val GLOW_SCALE_DELTA = 0.08f

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiChatMessagePreview() {
    GiPreviewSurface {
        var helpful by remember { mutableStateOf(true) }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiUserMessage(text = "Is Apple a buy?")
            GiThinkingIndicator(status = "Reading the news…")
            GiAssistantMessage(name = "GPT Investor", text = "Apple looks steady", streaming = true)
            GiAssistantMessage(
                name = "GPT Investor",
                text = "Apple looks steady. Services keep growing and phones sold well. Not financial advice."
            ) {
                GiIconButton(
                    icon = GiIcons.copy,
                    contentDescription = "Copy answer",
                    onClick = {},
                    variant = GiIconButtonVariant.Ghost
                )
                GiIconToggleButton(
                    checked = helpful,
                    onCheckedChange = { helpful = it },
                    icon = GiIcons.thumbsUp,
                    checkedIcon = GiIcons.thumbsUpFill,
                    contentDescription = "Helpful",
                    variant = GiIconButtonVariant.Ghost
                )
                GiIconToggleButton(
                    checked = false,
                    onCheckedChange = {},
                    icon = GiIcons.thumbsDown,
                    checkedIcon = GiIcons.thumbsDownFill,
                    contentDescription = "Not helpful",
                    variant = GiIconButtonVariant.Ghost
                )
            }
            GiAnswerError(message = "Couldn't finish this answer.", retryText = "Try again", onRetry = {})
        }
    }
}
