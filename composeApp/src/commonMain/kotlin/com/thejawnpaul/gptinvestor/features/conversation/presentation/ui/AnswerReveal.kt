package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.annotator.annotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import com.mikepenz.markdown.compose.components.MarkdownComponentModel
import com.mikepenz.markdown.compose.elements.MarkdownParagraph
import com.mikepenz.markdown.compose.elements.MarkdownText
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlin.math.ceil
import kotlinx.coroutines.delay

internal class AnswerPresentation(val text: String, val revealing: Boolean, val tail: AnswerTail?)

@Composable
internal fun rememberAnswerPresentation(messageId: Long, response: String, streaming: Boolean): AnswerPresentation {
    val reducedMotion = isReducedMotionEnabled()
    val streamedHere = remember(messageId) { streaming }
    val reveal = rememberAnswerReveal(
        messageId = messageId,
        text = response,
        streaming = streaming,
        animate = streamedHere
    )
    val revealing = streaming || (streamedHere && reveal.length < response.length)
    val cursorAlpha = animateFloatAsState(
        targetValue = if (revealing) 1f else 0f,
        animationSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised),
        label = "answerCursor"
    )
    val cursorFading by remember { derivedStateOf { cursorAlpha.value > 0f } }
    val showCursor = streamedHere && (revealing || cursorFading)
    val settled = reveal.length == reveal.previous && reveal.previous == reveal.beforePrevious
    val tail = if (streamedHere && (showCursor || !settled)) {
        AnswerTail(
            newest = reveal.length - reveal.previous,
            newer = reveal.previous - reveal.beforePrevious,
            showCursor = showCursor,
            blink = !reducedMotion && revealing,
            cursorAlpha = { cursorAlpha.value }
        )
    } else {
        null
    }
    val shown = if (!streamedHere || reveal.length >= response.length) {
        response
    } else {
        response.substring(0, reveal.length)
    }
    return AnswerPresentation(text = shown, revealing = revealing, tail = tail)
}

internal object AnswerStopSignal {
    var count by mutableIntStateOf(0)
}

internal class AnswerTail(
    val newest: Int,
    val newer: Int,
    val showCursor: Boolean,
    val blink: Boolean,
    val cursorAlpha: () -> Float
)

@Stable
private class AnswerReveal(initial: Int) {
    var length by mutableIntStateOf(initial)
        private set
    var previous by mutableIntStateOf(initial)
        private set
    var beforePrevious by mutableIntStateOf(initial)
        private set

    fun show(newLength: Int) {
        beforePrevious = previous
        previous = length
        length = newLength
    }

    fun showAll(total: Int) {
        length = total
        previous = total
        beforePrevious = total
    }
}

@Composable
private fun rememberAnswerReveal(messageId: Long, text: String, streaming: Boolean, animate: Boolean): AnswerReveal {
    val reducedMotion = isReducedMotionEnabled()
    val reveal = remember(messageId) { AnswerReveal(if (streaming && !reducedMotion) 0 else text.length) }
    val currentText by rememberUpdatedState(text)
    val currentStreaming by rememberUpdatedState(streaming)
    val stopsAtStart = remember(messageId) { AnswerStopSignal.count }
    if (!animate) return reveal
    LaunchedEffect(messageId, reducedMotion) {
        while (true) {
            val full = currentText
            when {
                reducedMotion || AnswerStopSignal.count != stopsAtStart -> reveal.showAll(full.length)
                reveal.length < full.length -> {
                    val waiting = countWords(full, reveal.length)
                    val words = when {
                        !currentStreaming -> ceil(waiting / FLUSH_TICKS.toFloat()).toInt().coerceAtLeast(1)
                        waiting > CATCH_UP_BACKLOG -> CATCH_UP_WORDS_PER_TICK
                        else -> WORDS_PER_TICK
                    }
                    reveal.show(advanceWords(full, reveal.length, words, complete = !currentStreaming))
                }
                else -> reveal.show(reveal.length)
            }
            val settled = reveal.length == reveal.previous && reveal.previous == reveal.beforePrevious
            if (!currentStreaming && reveal.length >= currentText.length && settled) break
            delay(REVEAL_TICK_MS)
        }
    }
    return reveal
}

private fun countWords(text: String, from: Int): Int {
    var count = 0
    var inWord = false
    for (i in from until text.length) {
        val word = !text[i].isWhitespace()
        if (word && !inWord) count++
        inWord = word
    }
    return count
}

private fun advanceWords(text: String, from: Int, words: Int, complete: Boolean): Int {
    var index = from
    repeat(words) {
        var cursor = index
        while (cursor < text.length && text[cursor].isWhitespace()) cursor++
        while (cursor < text.length && !text[cursor].isWhitespace()) cursor++
        if (cursor == text.length && !complete) return index
        if (cursor == index) return index
        index = cursor
    }
    return index
}

@Composable
internal fun StreamingParagraph(model: MarkdownComponentModel, tail: AnswerTail, fadeColor: Color) {
    val style = model.typography.paragraph
    val content = model.content
    val isLast = (model.node.endOffset until content.length).all { content[it].isWhitespace() }
    if (!isLast) {
        MarkdownParagraph(model.content, model.node, style = style)
        return
    }
    val settings = annotatorSettings()
    val styled = buildAnnotatedString {
        pushStyle(style.toSpanStyle())
        buildMarkdownAnnotatedString(content = model.content, node = model.node, annotatorSettings = settings)
        pop()
        val end = length
        val newestStart = (end - tail.newest).coerceAtLeast(0)
        val newerStart = (newestStart - tail.newer).coerceAtLeast(0)
        if (newerStart < newestStart) {
            addStyle(SpanStyle(color = fadeColor.copy(alpha = NEWER_WORDS_ALPHA)), newerStart, newestStart)
        }
        if (newestStart < end) {
            addStyle(SpanStyle(color = fadeColor.copy(alpha = NEWEST_WORDS_ALPHA)), newestStart, end)
        }
        if (tail.showCursor) appendInlineContent(ANSWER_CURSOR_ID, " ")
    }
    MarkdownText(styled, model.node, style = style)
}

@Composable
internal fun AnswerCursor(blink: Boolean, alpha: () -> Float) {
    var on by remember { mutableStateOf(true) }
    if (blink) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(CURSOR_BLINK_MS)
                on = !on
            }
        }
    }
    val color = GiTheme.colors.violet
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(CURSOR_WIDTH)
                .graphicsLayer { this.alpha = if (on || !blink) alpha() else 0f }
                .clip(RoundedCornerShape(CURSOR_RADIUS))
                .background(color)
        )
    }
}

internal const val ANSWER_CURSOR_ID = "answerCursor"
internal val CURSOR_SLOT_WIDTH = 6.sp
internal val CURSOR_HEIGHT = 16.sp
private val CURSOR_WIDTH = 2.dp
private val CURSOR_RADIUS = 1.dp
private const val CURSOR_BLINK_MS = 500L
private const val REVEAL_TICK_MS = 60L
private const val WORDS_PER_TICK = 2
private const val CATCH_UP_WORDS_PER_TICK = 5
private const val CATCH_UP_BACKLOG = 40
private const val FLUSH_TICKS = 6
private const val NEWEST_WORDS_ALPHA = 0.35f
private const val NEWER_WORDS_ALPHA = 0.7f
