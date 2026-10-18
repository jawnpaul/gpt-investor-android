package com.thejawnpaul.gptinvestor.features.history.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryScreenView
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenEvent
import com.thejawnpaul.gptinvestor.history_empty_action
import com.thejawnpaul.gptinvestor.history_empty_body
import com.thejawnpaul.gptinvestor.history_empty_title
import com.thejawnpaul.gptinvestor.history_loading
import com.thejawnpaul.gptinvestor.history_title
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiEmptyState
import com.thejawnpaul.gptinvestor.theme.components.GiEyebrow
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import org.jetbrains.compose.resources.stringResource

private const val SKELETON_ROWS = 4
private const val SKELETON_TITLE_FRACTION = 0.6f
private const val SKELETON_SUBTITLE_FRACTION = 0.85f

@Composable
fun HistoryScreen(state: HistoryScreenView, onEvent: (HistoryScreenEvent) -> Unit, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val scrolled = listState.canScrollBackward

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GiTheme.colors.bg)
    ) {
        GiTopBar(
            title = stringResource(Res.string.history_title),
            onBack = { onEvent(HistoryScreenEvent.GoBack) },
            backContentDescription = stringResource(Res.string.back),
            scrolled = scrolled
        )

        when {
            state.loading && state.list.isEmpty() -> HistorySkeleton()

            state.list.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = GiSpace.gutter),
                contentAlignment = Alignment.Center
            ) {
                GiEmptyState(
                    icon = GiIcons.clock,
                    title = stringResource(Res.string.history_empty_title),
                    body = stringResource(Res.string.history_empty_body),
                    actionText = stringResource(Res.string.history_empty_action),
                    onAction = { onEvent(HistoryScreenEvent.GoBack) }
                )
            }

            else -> LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = GiSpace.gutter,
                    end = GiSpace.gutter,
                    top = GiSpace.x2,
                    bottom = GiSpace.x10
                ),
                verticalArrangement = Arrangement.spacedBy(GiSpace.sectionGap)
            ) {
                state.list.forEach { (title, items) ->
                    item(key = title, contentType = "day") {
                        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
                            GiEyebrow(text = title, modifier = Modifier.semantics { heading() })
                            GiListGroup {
                                items.forEachIndexed { index, conversation ->
                                    if (index > 0) GiDivider()
                                    SingleHistoryItem(
                                        conversation = conversation,
                                        onClick = { id -> onEvent(HistoryScreenEvent.HistoryItemClicked(id)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySkeleton() {
    val loadingDescription = stringResource(Res.string.history_loading)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x2)
            .semantics { contentDescription = loadingDescription },
        verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)
    ) {
        GiSkeleton(modifier = Modifier.width(GiSpace.x10 + GiSpace.x4).height(GiSpace.x4))
        GiListGroup {
            repeat(SKELETON_ROWS) { index ->
                if (index > 0) GiDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(GiSpace.stockRow)
                        .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
                    verticalArrangement = Arrangement.spacedBy(GiSpace.x2, Alignment.CenterVertically)
                ) {
                    GiSkeleton(modifier = Modifier.fillMaxWidth(SKELETON_TITLE_FRACTION).height(GiSpace.x4))
                    GiSkeleton(modifier = Modifier.fillMaxWidth(SKELETON_SUBTITLE_FRACTION).height(GiSpace.x3))
                }
            }
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun HistoryScreenPreview() {
    GPTInvestorTheme {
        HistoryScreen(
            state = HistoryScreenView(
                list = mapOf(
                    "Today" to listOf(
                        StructuredConversation(
                            id = 1,
                            title = "Biggest tech stocks",
                            messageList = mutableListOf(
                                GenAiTextMessage(
                                    query = "What are the biggest tech stocks this year?",
                                    feedbackStatus = 0
                                )
                            ),
                            lastMessageTime = "16:03"
                        ),
                        StructuredConversation(id = 2, title = "JNJ dividend stock", lastMessageTime = "15:57")
                    )
                )
            ),
            onEvent = {}
        )
    }
}
