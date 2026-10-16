package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ask_a_follow_up
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.features.conversation.data.remote.SuggestionRemote
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.history
import com.thejawnpaul.gptinvestor.send
import com.thejawnpaul.gptinvestor.stop_answering
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiAskComposer
import com.thejawnpaul.gptinvestor.theme.components.GiChip
import com.thejawnpaul.gptinvestor.theme.components.GiChipStyle
import com.thejawnpaul.gptinvestor.theme.components.GiComposerBar
import com.thejawnpaul.gptinvestor.theme.components.GiComposerStop
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.thinking
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun StructuredConversationScreen(
    title: String,
    messages: List<GenAiMessage>,
    pendingQuery: String?,
    isAnswering: Boolean,
    inputQuery: String,
    suggestions: List<SuggestionRemote>,
    isGuest: Boolean,
    onNavigateUp: () -> Unit,
    onOpenHistory: () -> Unit,
    onInputQueryChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onClickFeedback: (messageId: Long, status: Int, reason: String?) -> Unit,
    onRetry: (query: String) -> Unit,
    onOpenCompany: (ticker: String) -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
    composerModifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val thread = rememberAnswerThreadState()
    val strings = answerStrings()
    val thinkingStatus = stringResource(Res.string.thinking)
    val threadMessages = rememberThreadMessages(messages)
    ScrollToNewQuestionEffect(
        listState = listState,
        questionCount = threadMessages.size + if (pendingQuery != null) 1 else 0
    )
    KeepThreadEndInViewEffect(listState)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Column {
                GiTopBar(
                    title = title,
                    onBack = onNavigateUp,
                    backContentDescription = stringResource(Res.string.back),
                    scrolled = true
                ) {
                    GiIconButton(
                        icon = GiIcons.clockCounterClockwise,
                        contentDescription = stringResource(Res.string.history),
                        onClick = onOpenHistory
                    )
                }
                if (isGuest) {
                    TopGuestLabel(
                        onClick = onSignUpClick,
                        modifier = Modifier.padding(
                            start = GiSpace.gutter,
                            end = GiSpace.gutter,
                            top = GiSpace.x3,
                            bottom = GiSpace.x3
                        )
                    )
                }
            }
        },
        bottomBar = {
            GiComposerBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
                docked = true
            ) {
                Column {
                    ConversationSuggestions(
                        visible = !isAnswering && pendingQuery == null && inputQuery.isEmpty(),
                        suggestions = suggestions,
                        onClick = onSuggestionClick
                    )
                    GiAskComposer(
                        value = inputQuery,
                        onValueChange = onInputQueryChange,
                        onSend = {
                            keyboardController?.hide()
                            onSendClick()
                        },
                        placeholder = stringResource(Res.string.ask_a_follow_up),
                        sendContentDescription = stringResource(Res.string.send),
                        modifier = composerModifier,
                        floating = false,
                        stop = if (isAnswering) {
                            GiComposerStop(
                                contentDescription = stringResource(Res.string.stop_answering),
                                onStop = onStopClick
                            )
                        } else {
                            null
                        },
                        largeTextMaxLines = COMPOSER_LARGE_TEXT_LINES
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = GiSpace.gutter,
                end = GiSpace.gutter,
                top = innerPadding.calculateTopPadding() + GiSpace.x5,
                bottom = innerPadding.calculateBottomPadding() + GiSpace.x5
            ),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x5)
        ) {
            answerThreadItems(
                messages = threadMessages,
                pendingQuery = pendingQuery,
                thinkingStatus = thinkingStatus,
                strings = strings,
                thread = thread,
                listState = listState,
                isGuest = isGuest,
                onCompanyClick = onOpenCompany,
                onCopy = onCopy,
                onShare = onShare,
                onFeedback = onClickFeedback,
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun KeepThreadEndInViewEffect(listState: LazyListState) {
    LaunchedEffect(listState) {
        var previous: ThreadEnd? = null
        snapshotFlow {
            ThreadEnd(padding = listState.layoutInfo.afterContentPadding, atEnd = !listState.canScrollForward)
        }.collect { current ->
            val last = previous
            val grewAtEnd = last != null && last.atEnd && current.padding > last.padding
            if (grewAtEnd) listState.dispatchRawDelta((current.padding - last.padding).toFloat())
            previous = if (grewAtEnd) current.copy(atEnd = true) else current
        }
    }
}

@Composable
private fun ConversationSuggestions(visible: Boolean, suggestions: List<SuggestionRemote>, onClick: (String) -> Unit) {
    val reducedMotion = isReducedMotionEnabled()
    var shown by remember { mutableStateOf(visible) }
    LaunchedEffect(visible) {
        if (visible) delay(SUGGESTIONS_DELAY_MS)
        shown = visible
    }
    val chips = remember(suggestions) {
        suggestions.mapNotNull { suggestion ->
            val query = suggestion.query?.ifBlank { null } ?: suggestion.label?.ifBlank { null }
            query?.let { SuggestionChip(label = suggestion.label?.ifBlank { null } ?: it, query = it) }
        }
    }
    val fadeIn = fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart))
    val fadeOut = fadeOut(tween(GiMotion.FAST, easing = EaseOutQuart))
    AnimatedVisibility(
        visible = shown && chips.isNotEmpty(),
        enter = if (reducedMotion) fadeIn else fadeIn + expandVertically(GiMotion.smooth()),
        exit = if (reducedMotion) fadeOut else fadeOut + shrinkVertically(GiMotion.smooth())
    ) {
        Row(
            modifier = Modifier
                .bleedHorizontally()
                .padding(bottom = GiSpace.x3)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = GiSpace.gutter),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            chips.forEach { chip ->
                GiChip(text = chip.label, onClick = { onClick(chip.query) }, style = GiChipStyle.Quiet)
            }
        }
    }
}

fun conversationTitle(conversation: StructuredConversation?, pendingQuery: String?): String =
    conversation?.title?.ifBlank { null }
        ?: conversation?.messageList?.firstNotNullOfOrNull { (it as? GenAiTextMessage)?.query }
        ?: pendingQuery.orEmpty()

private fun Modifier.bleedHorizontally(): Modifier = layout { measurable, constraints ->
    val bleed = GiSpace.gutter.roundToPx()
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = constraints.maxWidth + bleed * 2,
            maxWidth = constraints.maxWidth + bleed * 2
        )
    )
    layout(constraints.maxWidth, placeable.height) { placeable.place(-bleed, 0) }
}

private data class SuggestionChip(val label: String, val query: String)

private data class ThreadEnd(val padding: Int, val atEnd: Boolean)

private const val COMPOSER_LARGE_TEXT_LINES = 3
private const val SUGGESTIONS_DELAY_MS = GiMotion.SLOW.toLong()

private val previewMessages = listOf(
    GenAiTextMessage(
        id = 1,
        query = "What are the biggest tech stocks this year?",
        feedbackStatus = 1,
        response = "As of mid-2024, the biggest tech stocks are ranked by **market capitalisation**. Rankings " +
            "fluctuate daily, and biggest does not mean best investment."
    ),
    GenAiTextMessage(id = 2, query = "Which one is cheapest?", feedbackStatus = 0, loading = true)
)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun StructuredConversationScreenPreview() {
    GPTInvestorTheme {
        StructuredConversationScreen(
            title = "What are the biggest tech stocks this year?",
            messages = previewMessages,
            pendingQuery = null,
            isAnswering = true,
            inputQuery = "",
            suggestions = emptyList(),
            isGuest = false,
            onNavigateUp = {},
            onOpenHistory = {},
            onInputQueryChange = {},
            onSendClick = {},
            onStopClick = {},
            onSuggestionClick = {},
            onCopy = {},
            onShare = {},
            onClickFeedback = { _, _, _ -> },
            onRetry = {},
            onOpenCompany = {},
            onSignUpClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun StructuredConversationScreenIdlePreview() {
    GPTInvestorTheme {
        StructuredConversationScreen(
            title = "Biggest tech stocks",
            messages = previewMessages.take(1),
            pendingQuery = null,
            isAnswering = false,
            inputQuery = "",
            suggestions = listOf(
                SuggestionRemote(label = "Compare NVDA and AMD", query = "Compare NVDA and AMD"),
                SuggestionRemote(label = "Is Apple overvalued?", query = "Is Apple overvalued?")
            ),
            isGuest = false,
            onNavigateUp = {},
            onOpenHistory = {},
            onInputQueryChange = {},
            onSendClick = {},
            onStopClick = {},
            onSuggestionClick = {},
            onCopy = {},
            onShare = {},
            onClickFeedback = { _, _, _ -> },
            onRetry = {},
            onOpenCompany = {},
            onSignUpClick = {}
        )
    }
}
