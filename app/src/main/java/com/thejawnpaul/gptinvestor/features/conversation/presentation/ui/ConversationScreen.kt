package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.UnStructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.InputBar

@Composable
fun ConversationScreen(modifier: Modifier = Modifier, viewModel: ConversationViewModel) {
    val conversation = viewModel.conversation.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (conversation.value.conversation) {
            is DefaultConversation -> {
                val default = conversation.value.conversation as DefaultConversation
                DefaultConversationScreen(modifier = Modifier, conversation = default) {
                }
            }

            is StructuredConversation -> {
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
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultConversationScreen(
    modifier: Modifier = Modifier,
    conversation: DefaultConversation,
    onPromptClicked: (prompt: DefaultPrompt) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
            }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.back)
                )
            }

            Text(
                text = stringResource(R.string.ask_gpt_investor),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        // Ask About
        Text(
            text = stringResource(R.string.ask_about),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(16.dp)
        )

        // Flow row
        DefaultPrompts(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            prompts = conversation.prompts,
            onClick = onPromptClicked
        )
    }
}
