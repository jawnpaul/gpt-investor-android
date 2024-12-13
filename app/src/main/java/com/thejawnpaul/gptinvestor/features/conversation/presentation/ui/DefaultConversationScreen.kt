package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt

@Composable
fun DefaultConversationScreen(modifier: Modifier = Modifier, conversation: DefaultConversation, onPromptClicked: (prompt: DefaultPrompt) -> Unit, onNavigateUp: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            prompts = conversation.prompts,
            onClick = onPromptClicked
        )
    }
}
