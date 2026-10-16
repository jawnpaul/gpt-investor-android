package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.answer_error
import com.thejawnpaul.gptinvestor.answer_offline_body
import com.thejawnpaul.gptinvestor.answer_offline_title
import com.thejawnpaul.gptinvestor.copy
import com.thejawnpaul.gptinvestor.core.utility.toCurrency
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.answerFailed
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.helpful
import com.thejawnpaul.gptinvestor.inaccurate
import com.thejawnpaul.gptinvestor.not_actionable
import com.thejawnpaul.gptinvestor.not_financial_advice
import com.thejawnpaul.gptinvestor.not_helpful
import com.thejawnpaul.gptinvestor.others
import com.thejawnpaul.gptinvestor.share
import com.thejawnpaul.gptinvestor.show_less
import com.thejawnpaul.gptinvestor.show_more
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiAnswerActionLabels
import com.thejawnpaul.gptinvestor.theme.components.GiAnswerActions
import com.thejawnpaul.gptinvestor.theme.components.GiAnswerError
import com.thejawnpaul.gptinvestor.theme.components.GiAssistantMessage
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiFeedback
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiStockRow
import com.thejawnpaul.gptinvestor.theme.components.GiThinkingIndicator
import com.thejawnpaul.gptinvestor.theme.components.GiUserMessage
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.too_complex
import com.thejawnpaul.gptinvestor.try_again
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

class ThreadMessage(val message: GenAiTextMessage, val companies: List<CompanyDetailRemoteResponse>)

private fun answerFailed(message: GenAiTextMessage, companies: List<CompanyDetailRemoteResponse>): Boolean =
    message.answerFailed && !(companies.isNotEmpty() && message.response.isNullOrBlank())

private val ThreadMessage.failed: Boolean
    get() = answerFailed(message, companies)

private val ThreadMessage.answered: Boolean
    get() = !message.loading && !failed

private fun threadMessages(messages: List<GenAiMessage>): List<ThreadMessage> {
    val texts = messages.filterIsInstance<GenAiTextMessage>()
    val entities = messages.filterIsInstance<GenAiEntityMessage>().filter { it.entity != null }
    val thread = texts.map { text ->
        val own = entities.filter { entity -> texts.lastOrNull { it.id < entity.id }?.id == text.id }
        val companies = when {
            own.isNotEmpty() -> own.mapNotNull { it.entity }
            !text.loading && text.response.isNullOrBlank() ->
                listOfNotNull(entities.lastOrNull { it.id < text.id }?.entity)
            else -> emptyList()
        }
        ThreadMessage(message = text, companies = companies)
    }
    return thread.filterIndexed { index, item ->
        !item.failed || thread.drop(index + 1).none { it.message.query == item.message.query && it.answered }
    }
}

@Composable
fun rememberThreadMessages(messages: List<GenAiMessage>): List<ThreadMessage> {
    val cache = remember { ThreadMessageCache() }
    return remember(messages) { cache.reuse(threadMessages(messages)) }
}

private class ThreadMessageCache {
    private var last: List<ThreadMessage> = emptyList()

    fun reuse(next: List<ThreadMessage>): List<ThreadMessage> {
        val reused = next.mapIndexed { index, item ->
            val old = last.getOrNull(index)
            if (old != null && old.message == item.message && old.companies.sameItems(item.companies)) old else item
        }
        last = reused
        return reused
    }

    private fun <T> List<T>.sameItems(other: List<T>): Boolean =
        size == other.size && indices.all { this[it] === other[it] }
}

data class AnswerStrings(
    val name: String,
    val disclaimer: String,
    val showMore: String,
    val showLess: String,
    val labels: GiAnswerActionLabels,
    val reasons: List<String>,
    val answerError: String,
    val offlineTitle: String,
    val offlineBody: String,
    val tryAgain: String
)

@Composable
fun answerStrings(): AnswerStrings = AnswerStrings(
    name = stringResource(Res.string.gpt_investor),
    disclaimer = stringResource(Res.string.not_financial_advice),
    showMore = stringResource(Res.string.show_more),
    showLess = stringResource(Res.string.show_less),
    labels = GiAnswerActionLabels(
        copy = stringResource(Res.string.copy),
        helpful = stringResource(Res.string.helpful),
        notHelpful = stringResource(Res.string.not_helpful),
        share = stringResource(Res.string.share)
    ),
    reasons = listOf(
        stringResource(Res.string.too_complex),
        stringResource(Res.string.inaccurate),
        stringResource(Res.string.not_actionable),
        stringResource(Res.string.others)
    ),
    answerError = stringResource(Res.string.answer_error),
    offlineTitle = stringResource(Res.string.answer_offline_title),
    offlineBody = stringResource(Res.string.answer_offline_body),
    tryAgain = stringResource(Res.string.try_again)
)

@Stable
class AnswerThreadState(private val reducedMotion: Boolean) {
    val expanded = mutableStateMapOf<Long, Boolean>()
    var showReasonsFor by mutableStateOf<Long?>(null)
    var instantCollapseFor by mutableStateOf<Long?>(null)
    var settlingCollapse by mutableStateOf(false)
    val itemSpec by derivedStateOf { ThreadItemSpec(animatePlacement = !reducedMotion && !settlingCollapse) }
}

@Composable
fun rememberAnswerThreadState(): AnswerThreadState {
    val reducedMotion = isReducedMotionEnabled()
    val state = remember(reducedMotion) { AnswerThreadState(reducedMotion) }
    LaunchedEffect(state, state.settlingCollapse) {
        if (state.settlingCollapse) {
            repeat(SETTLE_FRAMES) { withFrameNanos { } }
            state.settlingCollapse = false
        }
    }
    LaunchedEffect(state, state.showReasonsFor) {
        if (state.showReasonsFor != null) {
            delay(REASONS_TIMEOUT_MS)
            state.showReasonsFor = null
        }
    }
    return state
}

@Composable
fun ScrollToNewQuestionEffect(listState: LazyListState, questionCount: Int) {
    var scrolledQuestionCount by remember { mutableIntStateOf(questionCount) }
    LaunchedEffect(questionCount) {
        val grew = questionCount > scrolledQuestionCount
        scrolledQuestionCount = questionCount
        if (grew) {
            withFrameNanos { }
            listState.animateScrollToItem((listState.layoutInfo.totalItemsCount - ITEMS_AFTER_BUBBLE).coerceAtLeast(0))
        }
    }
}

fun LazyListScope.answerThreadItems(
    messages: List<ThreadMessage>,
    pendingQuery: String?,
    thinkingStatus: String,
    strings: AnswerStrings,
    thread: AnswerThreadState,
    listState: LazyListState,
    isGuest: Boolean,
    answerOffline: Boolean,
    onCompanyClick: (String) -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onFeedback: (messageId: Long, status: Int, reason: String?) -> Unit,
    onRetry: (query: String) -> Unit
) {
    messages.forEachIndexed { index, threadMessage ->
        val message = threadMessage.message
        val isLatest = index == messages.lastIndex && pendingQuery == null
        item(key = "q-$index", contentType = "question") {
            GiUserMessage(text = message.query, modifier = threadItem(thread.itemSpec))
        }
        if (message.loading && message.response.isNullOrBlank() && threadMessage.companies.isEmpty()) {
            item(key = "t-$index", contentType = "thinking") {
                GiThinkingIndicator(status = thinkingStatus, modifier = threadItem(thread.itemSpec))
            }
        } else {
            item(key = "a-$index", contentType = "answer") {
                ThreadAnswer(
                    modifier = threadItem(thread.itemSpec),
                    message = message,
                    companies = threadMessage.companies,
                    strings = strings,
                    onCompanyClick = onCompanyClick,
                    collapsed = !(thread.expanded[message.id] ?: isLatest),
                    animateCollapse = thread.instantCollapseFor != message.id,
                    onToggleCollapse = {
                        val expandedNow = thread.expanded[message.id] ?: isLatest
                        val instant = expandedNow && listState.keepNextItemInPlace("a-$index")
                        thread.instantCollapseFor = if (instant) message.id else null
                        thread.settlingCollapse = instant
                        thread.expanded[message.id] = !expandedNow
                    },
                    isGuest = isGuest,
                    offline = answerOffline && isLatest,
                    showReasons = thread.showReasonsFor == message.id,
                    onCopy = onCopy,
                    onShare = onShare,
                    onFeedback = onFeedback,
                    onRetry = { onRetry(message.query) },
                    onNotHelpful = { thread.showReasonsFor = message.id },
                    onReasonPick = { thread.showReasonsFor = null }
                )
            }
        }
    }
    if (pendingQuery != null) {
        item(key = "q-${messages.size}", contentType = "question") {
            GiUserMessage(text = pendingQuery, modifier = threadItem(thread.itemSpec))
        }
        item(key = "t-${messages.size}", contentType = "thinking") {
            GiThinkingIndicator(status = thinkingStatus, modifier = threadItem(thread.itemSpec))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThreadAnswer(
    message: GenAiTextMessage,
    companies: List<CompanyDetailRemoteResponse>,
    strings: AnswerStrings,
    onCompanyClick: (String) -> Unit,
    collapsed: Boolean,
    animateCollapse: Boolean,
    onToggleCollapse: () -> Unit,
    isGuest: Boolean,
    offline: Boolean,
    showReasons: Boolean,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onFeedback: (messageId: Long, status: Int, reason: String?) -> Unit,
    onRetry: () -> Unit,
    onNotHelpful: () -> Unit,
    onReasonPick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val failed = answerFailed(message, companies)
    val response = if (failed) "" else message.response.orEmpty()
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        GiAssistantMessage(
            name = strings.name,
            streaming = message.loading,
            disclaimer = if (failed) null else strings.disclaimer,
            collapsed = collapsed,
            animateCollapse = animateCollapse,
            onToggleCollapse = onToggleCollapse,
            showMoreText = strings.showMore,
            showLessText = strings.showLess,
            actions = {
                if (response.isNotBlank()) {
                    GiAnswerActions(
                        labels = strings.labels,
                        onCopy = { onCopy(response) },
                        onShare = { onShare(response) },
                        feedback = when (message.feedbackStatus) {
                            1 -> GiFeedback.Helpful
                            -1 -> GiFeedback.NotHelpful
                            else -> GiFeedback.None
                        },
                        onHelpful = if (isGuest) {
                            null
                        } else {
                            { onFeedback(message.id, 1, null) }
                        },
                        onNotHelpful = if (isGuest) {
                            null
                        } else {
                            {
                                onFeedback(message.id, -1, null)
                                onNotHelpful()
                            }
                        }
                    )
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                companies.forEach { company ->
                    AnswerCompanyCard(
                        company = company,
                        onClick = { onCompanyClick(company.ticker) }
                    )
                }
                if (failed) {
                    GiAnswerError(
                        title = if (offline) strings.offlineTitle else null,
                        message = if (offline) strings.offlineBody else strings.answerError,
                        retryText = strings.tryAgain,
                        onRetry = onRetry
                    )
                } else if (response.isNotBlank()) {
                    AnswerMarkdown(text = response)
                }
            }
        }
        AnimatedVisibility(
            visible = showReasons,
            enter = fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart)) + expandVertically(GiMotion.smooth()),
            exit = fadeOut(tween(GiMotion.FAST, easing = EaseOutQuart)) + shrinkVertically(GiMotion.smooth())
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                strings.reasons.forEach { reason ->
                    GiButton(
                        text = reason,
                        onClick = {
                            onFeedback(message.id, -1, reason)
                            onReasonPick()
                        },
                        size = GiButtonSize.Small,
                        variant = GiButtonVariant.Secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerCompanyCard(
    company: CompanyDetailRemoteResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = company.name ?: company.ticker
    val subtitle = company.price?.let { "${company.ticker} · ${it.toCurrency("$")}" } ?: company.ticker
    GiListGroup(modifier = modifier) {
        GiStockRow(
            stock = tickerStock(
                ticker = company.ticker,
                title = name,
                subtitle = subtitle,
                percent = company.change?.toDouble(),
                imageUrl = company.imageUrl?.ifBlank { null }
            ),
            onClick = onClick,
            showCaret = true
        )
    }
}

private fun LazyListState.keepNextItemInPlace(answerKey: String): Boolean {
    val items = layoutInfo.visibleItemsInfo
    val answer = items.firstOrNull { it.key == answerKey } ?: return false
    if (answer.offset >= 0) return false
    val nextIndex = answer.index + 1
    if (nextIndex < layoutInfo.totalItemsCount) {
        val nextOffset = items.firstOrNull { it.index == nextIndex }?.offset
            ?: (answer.offset + answer.size + layoutInfo.mainAxisItemSpacing)
        requestScrollToItem(nextIndex, -nextOffset)
    }
    return true
}

class ThreadItemSpec(animatePlacement: Boolean) {
    val fadeIn = tween<Float>(GiMotion.BASE, easing = EaseOutQuart)
    val fadeOut = tween<Float>(GiMotion.FAST, easing = EaseOutQuart)
    val placement = if (animatePlacement) GiMotion.smooth<IntOffset>() else null
}

fun LazyItemScope.threadItem(spec: ThreadItemSpec): Modifier =
    Modifier.animateItem(fadeInSpec = spec.fadeIn, placementSpec = spec.placement, fadeOutSpec = spec.fadeOut)

private const val ITEMS_AFTER_BUBBLE = 2
private const val REASONS_TIMEOUT_MS = 15_000L
private const val SETTLE_FRAMES = 2
