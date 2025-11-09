package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.state.ConversationView
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.WaitlistBottomSheetContent
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun ConversationScreen(
    modifier: Modifier,
    state: ConversationView,
    chatInput: String? = null,
    title: String? = null,
    onEvent: (ConversationEvent) -> Unit,
    onAction: (ConversationAction) -> Unit
) {
    LaunchedEffect(key1 = chatInput) {
        if (chatInput != null) {
            val decodedChatInput = URLDecoder.decode(chatInput, StandardCharsets.UTF_8.toString())
            if (title != null) {
                val decodedTitle =
                    title.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                onEvent(
                    ConversationEvent.DefaultPromptClicked(
                        prompt = DefaultPrompt(
                            title = decodedTitle,
                            query = decodedChatInput
                        )
                    )
                )
            } else {
                onEvent(ConversationEvent.SendPrompt(query = decodedChatInput))
            }
        }
    }

    if (state.showWaitListBottomSheet) {
        GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
            onEvent(ConversationEvent.UpgradeModel(showBottomSheet = false))
        }) {
            WaitlistBottomSheetContent(
                modifier = Modifier,
                options = state.waitlistAvailableOptions,
                selectedOptions = state.selectedWaitlistOptions,
                onOptionSelected = {
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

    Box(modifier = modifier.fillMaxSize()) {
        when (state.conversation) {
            is DefaultConversation -> {
                val default = state.conversation
                DefaultConversationScreen(
                    modifier = Modifier,
                    conversation = default,
                    onNavigateUp = {
                        onAction(ConversationAction.OnGoBack)
                    },
                    onPromptClicked = { prompt ->
                        onEvent(ConversationEvent.DefaultPromptClicked(prompt))
                    },
                    inputQuery = state.query,
                    onInputQueryChanged = { input ->
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
                    }
                )
            }

            is StructuredConversation -> {
                // Try to pass in the VM instance down
                StructuredConversationScreen(
                    modifier = Modifier,
                    conversation = state.conversation,
                    onNavigateUp = { onAction(ConversationAction.OnGoBack) },
                    text = state.genText,
                    onClickNews = {
                        onAction(ConversationAction.OnGoToWebView(url = it))
                    },
                    onClickFeedback = { messageId, status, reason ->
                        onEvent(ConversationEvent.SendFeedback(messageId, status, reason))
                    },
                    onCopy = { text ->

                        onEvent(ConversationEvent.CopyToClipboard(text))
                    },
                    inputQuery = state.query,
                    onInputQueryChanged = { input ->
                        onEvent(ConversationEvent.UpdateInputQuery(input))
                    },
                    onSendClick = {
                        onEvent(ConversationEvent.SendPrompt(query = state.query))
                    },
                    companyName = "",
                    onClickSuggestedPrompt = {
                        onEvent(ConversationEvent.SuggestedPromptClicked(it))
                    },
                    availableModels = state.availableModels,
                    selectedModel = state.selectedModel,
                    onModelChange = {
                        onEvent(ConversationEvent.ModelChanged(model = it))
                    },
                    onUpgradeModel = { showBottomSheet, modelId ->
                        onEvent(ConversationEvent.UpgradeModel(showBottomSheet, modelId))
                    }
                )
            }

            else -> {
            }
        }
    }
}
