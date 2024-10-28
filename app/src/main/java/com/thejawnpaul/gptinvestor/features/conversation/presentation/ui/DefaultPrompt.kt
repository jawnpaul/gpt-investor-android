package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt

@Composable
fun SingleDefaultPrompt(modifier: Modifier = Modifier, prompt: DefaultPrompt, onClick: (prompt: DefaultPrompt) -> Unit) {
    Card(onClick = { onClick(prompt) }, modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(text = prompt.title, modifier = Modifier.padding(8.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DefaultPrompts(modifier: Modifier, prompts: List<DefaultPrompt>, onClick: (prompt: DefaultPrompt) -> Unit) {
    FlowRow(modifier) {
        prompts.forEach {
            SingleDefaultPrompt(modifier = Modifier, prompt = it, onClick = onClick)
        }
    }
}
