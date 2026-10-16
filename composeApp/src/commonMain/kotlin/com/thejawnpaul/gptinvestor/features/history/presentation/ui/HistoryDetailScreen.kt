package com.thejawnpaul.gptinvestor.features.history.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.RateLimitBottomSheetContent
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.StructuredConversationScreen
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.conversationTitle
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryConversationView
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.WaitlistBottomSheetContent

@Composable
fun HistoryDetailScreen(
    state: HistoryConversationView,
    onEvent: (HistoryDetailEvent) -> Unit,
    onAction: (HistoryDetailAction) -> Unit,
    onUpgradeFromRateLimit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (state.showRateLimitBottomSheet) {
            GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
                onEvent(HistoryDetailEvent.ShowRateLimitBottomSheet(showBottomSheet = false))
            }) {
                RateLimitBottomSheetContent(
                    modifier = Modifier,
                    onUpgrade = onUpgradeFromRateLimit
                )
            }
        }

        if (state.showWaitListBottomSheet) {
            GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
                onEvent(HistoryDetailEvent.UpgradeModel(showBottomSheet = false))
            }) {
                WaitlistBottomSheetContent(
                    modifier = Modifier,
                    options = state.waitlistAvailableOptions,
                    selectedOptions = state.selectedWaitlistOptions,
                    onSelectOption = {
                        onEvent(HistoryDetailEvent.SelectWaitlistOption(it))
                    },
                    onJoinWaitList = {
                        onEvent(HistoryDetailEvent.JoinWaitlist)
                    },
                    onDismiss = {
                        onEvent(HistoryDetailEvent.UpgradeModel(showBottomSheet = false))
                    }
                )
            }
        }

        val structured = state.conversation as? StructuredConversation
        if (structured != null || state.pendingQuery != null) {
            StructuredConversationScreen(
                title = conversationTitle(structured, state.pendingQuery),
                messages = structured?.messageList.orEmpty(),
                pendingQuery = state.pendingQuery,
                isAnswering = state.loading,
                inputQuery = state.query,
                suggestions = structured?.suggestedPrompts.orEmpty(),
                isGuest = state.isGuest,
                onNavigateUp = { onAction(HistoryDetailAction.OnGoBack) },
                onOpenHistory = { onEvent(HistoryDetailEvent.GoToHistory) },
                onInputQueryChange = { onEvent(HistoryDetailEvent.UpdateInputQuery(it)) },
                onSendClick = { onEvent(HistoryDetailEvent.GetInputResponse) },
                onStopClick = { onEvent(HistoryDetailEvent.StopAnswering) },
                onSuggestionClick = { onEvent(HistoryDetailEvent.ClickSuggestedPrompt(it)) },
                onCopy = { onEvent(HistoryDetailEvent.CopyToClipboard(it)) },
                onShare = { onEvent(HistoryDetailEvent.ShareAnswer(it)) },
                onClickFeedback = { messageId, status, reason ->
                    onEvent(HistoryDetailEvent.SendFeedback(messageId, status, reason))
                },
                onRetry = { onEvent(HistoryDetailEvent.RetryAnswer(it)) },
                onOpenCompany = { onEvent(HistoryDetailEvent.OpenCompany(it)) },
                onSignUpClick = { onEvent(HistoryDetailEvent.GoToSignUp) }
            )
        }
    }
}
