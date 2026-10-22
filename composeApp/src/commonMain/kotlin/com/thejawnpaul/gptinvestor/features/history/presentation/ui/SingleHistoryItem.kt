package com.thejawnpaul.gptinvestor.features.history.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.conversationTitle
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiListRow
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle

@Composable
fun SingleHistoryItem(
    conversation: StructuredConversation,
    onClick: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = conversationTitle(conversation, pendingQuery = null)
    val firstQuestion = conversation.messageList
        .firstNotNullOfOrNull { it as? GenAiTextMessage }
        ?.query
        ?.takeIf { it.isNotBlank() && it != title }
    GiListRow(
        title = title,
        modifier = modifier,
        subtitle = firstQuestion,
        onClick = { onClick(conversation.id) },
        trailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                conversation.lastMessageTime?.let { time ->
                    GiText(text = time, style = GiTextStyle.Meta)
                }
                Icon(
                    imageVector = GiIcons.caretRight,
                    contentDescription = null,
                    modifier = Modifier.size(GiIconSize.sm),
                    tint = GiTheme.colors.muted
                )
            }
        }
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SingleHistoryItemPreview() {
    GPTInvestorTheme {
        GiListGroup {
            SingleHistoryItem(
                onClick = {},
                conversation = StructuredConversation(
                    id = 0,
                    title = "Biggest tech stocks",
                    messageList = mutableListOf(
                        GenAiTextMessage(query = "What are the biggest tech stocks this year?", feedbackStatus = 0)
                    ),
                    lastMessageTime = "16:03"
                )
            )
        }
    }
}
