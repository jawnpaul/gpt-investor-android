package com.thejawnpaul.gptinvestor.features.history.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation

@Composable
fun SingleHistoryItem(
    modifier: Modifier = Modifier,
    conversation: StructuredConversation,
    onClick: (id: Long) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(conversation.id) }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                conversation.title,
                modifier = Modifier.weight(1.2f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Image(
                painterResource(R.drawable.arrow_right),
                contentDescription = null,
                modifier = Modifier
                    .weight(0.2f)
            )
        }

        HorizontalDivider()
    }
}