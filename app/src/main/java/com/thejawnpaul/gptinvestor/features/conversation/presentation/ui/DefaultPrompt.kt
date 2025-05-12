package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.theme.gptInvestorColors
import kotlinx.coroutines.delay

@Composable
fun SingleDefaultPrompt(modifier: Modifier = Modifier, prompt: DefaultPrompt, onClick: (prompt: DefaultPrompt) -> Unit) {
    Surface(
        onClick = { onClick(prompt) },
        modifier = modifier.padding(horizontal = 8.dp),
        shape = RoundedCornerShape(corner = CornerSize(12.dp))
    ) {
        Text(
            text = prompt.query,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = gptInvestorColors.textColors.secondary50
        )
    }
}

@Composable
fun DefaultPrompts(modifier: Modifier, prompts: List<DefaultPrompt>, onClick: (prompt: DefaultPrompt) -> Unit) {
    var currentPrompts by remember { mutableStateOf(listOf<DefaultPrompt>()) }
    var visible by remember { mutableStateOf(true) }

    // Loop every 5 seconds: fade out -> update prompts -> fade in
    LaunchedEffect(prompts) {
        while (true) {
            visible = false
            delay(2000)
            currentPrompts = if (prompts.size >= 2) prompts.shuffled().take(2) else prompts
            visible = true
            delay(5000L)
        }
    }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = stringResource(R.string.type_something_like),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = gptInvestorColors.textColors.secondary50
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 3000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 3000))
        ) {
            Column {
                currentPrompts.forEach {
                    SingleDefaultPrompt(
                        prompt = it,
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
