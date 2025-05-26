package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationEvent
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.InputBar

@Composable
fun ConversationScreen(modifier: Modifier, viewModel: ConversationViewModel, navController: NavController, chatInput: String? = null) {
    val conversation = viewModel.conversation.collectAsStateWithLifecycle()
    val genText = viewModel.genText.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = chatInput) {
        if (chatInput != null) {
            viewModel.updateInput(input = chatInput)
            viewModel.getInputResponse()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (conversation.value.conversation) {
            is DefaultConversation -> {
                val default = conversation.value.conversation as DefaultConversation
                DefaultConversationScreen(
                    modifier = Modifier,
                    conversation = default,
                    onNavigateUp = { navController.navigateUp() },
                    onPromptClicked = { prompt ->
                        viewModel.getDefaultPromptResponse(prompt)
                    }
                )
            }

            is StructuredConversation -> {
                // Try to pass in the VM instance down
                StructuredConversationScreen(
                    modifier = Modifier,
                    conversation = conversation.value.conversation as StructuredConversation,
                    onNavigateUp = { navController.navigateUp() },
                    text = genText.value,
                    onClickNews = {
                        navController.navigate(Screen.WebViewScreen.createRoute(it))
                    },
                    onClickFeedback = { messageId, status, reason ->
                        viewModel.handleEvent(
                            ConversationEvent.SendFeedback(
                                messageId,
                                status,
                                reason
                            )
                        )
                    },
                    onCopy = { text ->
                        viewModel.handleEvent(ConversationEvent.CopyToClipboard(text))
                    },
                    inputQuery = conversation.value.query,
                    onInputQueryChanged = { input ->
                        viewModel.updateInput(input = input)
                    },
                    onSendClick = {
                    },
                    companyName = "default company name",
                    onClickSuggestedPrompt = {
                        viewModel.getSuggestedPromptResponse(it)
                    }
                )
            }

            else -> {
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
                .align(Alignment.BottomStart)
        ) {
            when (conversation.value.conversation) {
                is StructuredConversation -> {
                    val a = conversation.value.conversation as StructuredConversation

                    if (a.suggestedPrompts.isNotEmpty()) {
                        // Follow up questions
                        FollowUpQuestions(
                            modifier = Modifier,
                            entity = null,
                            list = a.suggestedPrompts,
                            onClick = { prompt ->
                                viewModel.getSuggestedPromptResponse(prompt.query)
                            }
                        )
                    }
                }

                else -> {
                }
            }

            InputBar(
                modifier = Modifier
                    .fillMaxWidth()
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
                shouldRequestFocus = true
            )
        }
    }
}
