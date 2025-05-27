package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.state.ConversationView
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationEvent

@Composable
fun ConversationScreen(modifier: Modifier, state: ConversationView, chatInput: String? = null, onEvent: (ConversationEvent) -> Unit, onAction: (ConversationAction) -> Unit) {
    LaunchedEffect(key1 = chatInput) {
        if (chatInput != null) {
            onEvent(ConversationEvent.SendPrompt(chatInput))
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
                    },
                    companyName = "",
                    onClickSuggestedPrompt = {
                        onEvent(ConversationEvent.SuggestedPromptClicked(it))
                    }
                )
            }

            else -> {
            }
        }
    }
}
