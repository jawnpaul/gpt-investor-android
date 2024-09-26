package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.UnStructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.InputBar

@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel,
    navController: NavController
) {
    val conversation = viewModel.conversation.collectAsStateWithLifecycle()
    val genText = viewModel.genText.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
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
                    onClickNews = {}
                )
            }

            is UnStructuredConversation -> {
            }
        }

        InputBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .windowInsetsPadding(
                    WindowInsets.ime.exclude(
                        WindowInsets.navigationBars
                    )
                ),
            input = conversation.value.query,
            contentPadding = PaddingValues(0.dp),
            sendEnabled = conversation.value.enableSend,
            onInputChanged = { input ->
                viewModel.updateInput(input = input)
            },
            onSendClick = {
                viewModel.getInputResponse()
            },
            placeholder = "Ask anything about stocks",
            shouldRequestFocus = true
        )
    }
}
