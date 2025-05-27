package com.thejawnpaul.gptinvestor.features.history.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.StructuredConversationScreen
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryConversationView
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailAction
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailEvent

@Composable
fun HistoryDetailScreen(modifier: Modifier, conversationId: String, state: HistoryConversationView, onEvent: (HistoryDetailEvent) -> Unit, onAction: (HistoryDetailAction) -> Unit) {
    LaunchedEffect(conversationId) {
        onEvent(HistoryDetailEvent.GetHistory(conversationId.toLong()))
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (state.conversation) {
            is DefaultConversation -> {
            }

            is StructuredConversation -> {
                StructuredConversationScreen(
                    modifier = Modifier,
                    conversation = state.conversation,
                    onNavigateUp = {
                        onAction(HistoryDetailAction.OnGoBack)
                    },
                    text = state.genText,
                    onClickNews = {
                        onAction(HistoryDetailAction.OnGoToWebView(it))
                    },
                    onClickFeedback = { messageId, status, reason ->
                        onEvent(HistoryDetailEvent.SendFeedback(messageId, status, reason))
                    },
                    onCopy = { text ->
                    },
                    inputQuery = state.query,
                    onInputQueryChanged = { input ->
                        onEvent(HistoryDetailEvent.UpdateInputQuery(input))
                    },
                    onSendClick = {
                        onEvent(HistoryDetailEvent.GetInputResponse)
                    },
                    companyName = "",
                    onClickSuggestedPrompt = {
                        onEvent(HistoryDetailEvent.ClickSuggestedPrompt(it))
                    }
                )
            }

            else -> {
            }
        }

        /*InputBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .windowInsetsPadding(
                    WindowInsets.ime
                )
                .navigationBarsPadding(),
            input = conversation.value.query,
            contentPadding = PaddingValues(0.dp),
            sendEnabled = conversation.value.enableSend,
            onInputChanged = { input ->
                viewModel.updateInput(input = input)
            },
            onSendClick = {
                keyboardController?.hide()
                viewModel.getInputResponse()
            },
            placeholder = "Ask anything about stocks",
            shouldRequestFocus = false
        )*/
    }
}
