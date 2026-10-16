package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.state.ConversationView
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.WaitlistBottomSheetContent

@Composable
fun ConversationScreen(
    state: ConversationView,
    onEvent: (ConversationEvent) -> Unit,
    onAction: (ConversationAction) -> Unit,
    onUpgradeFromRateLimit: () -> Unit = {},
    modifier: Modifier = Modifier,
    composerModifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (state.showWaitListBottomSheet) {
            GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
                onEvent(ConversationEvent.UpgradeModel(showBottomSheet = false))
            }) {
                WaitlistBottomSheetContent(
                    modifier = Modifier,
                    options = state.waitlistAvailableOptions,
                    selectedOptions = state.selectedWaitlistOptions,
                    onSelectOption = {
                        onEvent(ConversationEvent.SelectWaitlistOption(it))
                    },
                    onJoinWaitList = {
                        onEvent(ConversationEvent.JoinWaitlist)
                    },
                    onDismiss = {
                        onEvent(ConversationEvent.UpgradeModel(showBottomSheet = false))
                    }
                )
            }
        }

        if (state.showRateLimitBottomSheet) {
            GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
                onEvent(ConversationEvent.ShowRateLimitBottomSheet(showBottomSheet = false))
            }) {
                RateLimitBottomSheetContent(
                    modifier = Modifier,
                    onUpgrade = onUpgradeFromRateLimit
                )
            }
        }

        val structured = state.conversation as? StructuredConversation
        when {
            structured != null || state.pendingQuery != null -> {
                StructuredConversationScreen(
                    title = conversationTitle(structured, state.pendingQuery),
                    messages = structured?.messageList.orEmpty(),
                    pendingQuery = state.pendingQuery,
                    isAnswering = state.loading,
                    inputQuery = state.query,
                    suggestions = structured?.suggestedPrompts.orEmpty(),
                    isGuest = state.isGuest,
                    onNavigateUp = { onAction(ConversationAction.OnGoBack) },
                    onOpenHistory = { onEvent(ConversationEvent.GoToHistory) },
                    onInputQueryChange = { onEvent(ConversationEvent.UpdateInputQuery(it)) },
                    onSendClick = { onEvent(ConversationEvent.SendPrompt(query = state.query)) },
                    onStopClick = { onEvent(ConversationEvent.StopAnswering) },
                    onSuggestionClick = { onEvent(ConversationEvent.SuggestedPromptClicked(it)) },
                    onCopy = { onEvent(ConversationEvent.CopyToClipboard(it)) },
                    onShare = { onEvent(ConversationEvent.ShareAnswer(it)) },
                    onClickFeedback = { messageId, status, reason ->
                        onEvent(ConversationEvent.SendFeedback(messageId, status, reason))
                    },
                    onOpenCompany = { onEvent(ConversationEvent.OpenCompany(it)) },
                    onSignUpClick = { onEvent(ConversationEvent.GoToSignUp) },
                    composerModifier = composerModifier
                )
            }

            state.conversation is DefaultConversation -> {
                val default = state.conversation
                DefaultConversationScreen(
                    modifier = Modifier,
                    conversation = default,
                    isGuest = state.isGuest,
                    onNavigateUp = {
                        onAction(ConversationAction.OnGoBack)
                    },
                    onPromptClick = { prompt ->
                        onEvent(ConversationEvent.DefaultPromptClicked(prompt))
                    },
                    inputQuery = state.query,
                    onInputQueryChange = { input ->
                        onEvent(ConversationEvent.UpdateInputQuery(input))
                    },
                    onSendClick = {
                        onEvent(ConversationEvent.SendPrompt())
                    },
                    availableModels = state.availableModels,
                    selectedModel = state.selectedModel,
                    onModelChange = {
                        onEvent(ConversationEvent.ModelChanged(model = it))
                    },
                    onUpgradeModel = { showBottomSheet, modelId ->
                        onEvent(ConversationEvent.UpgradeModel(showBottomSheet, modelId))
                    },
                    onSignUpClick = {
                        onEvent(ConversationEvent.GoToSignUp)
                    }
                )
            }
        }
    }
}
