package com.thejawnpaul.gptinvestor.features.history.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun SingleHistoryItem(modifier: Modifier, conversation: StructuredConversation, onClick: (id: Long) -> Unit) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            .clickable(indication = null, interactionSource = null, onClick = {
                onClick(conversation.id)
            }),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(0.8f),
            text = conversation.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = conversation.lastMessageTime ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = gptInvestorColors.textColors.secondary50
        )
    }
}

@Preview
@Composable
fun ItemPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                SingleHistoryItem(
                    modifier = Modifier,
                    conversation = StructuredConversation(
                        id = 0,
                        title = "What factors are influencing Netflix stock price today?",
                        lastMessageTime = "10:00"
                    )
                ) { }
            }
        }
    }
}
