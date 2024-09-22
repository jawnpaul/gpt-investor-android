package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.ExpandableRichText
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.ui.theme.GPTInvestorTheme

@Composable
fun StructuredConversationScreen(
    modifier: Modifier = Modifier,
    conversation: StructuredConversation,
    onNavigateUp: () -> Unit,
    text: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
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
                text = conversation.title,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        if (conversation.messageList.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = conversation.messageList,
                    key = { item -> item.id }
                ) { genAiMessage ->
                    SingleStructuredResponse(
                        modifier = Modifier,
                        genAiMessage = genAiMessage,
                        text = text
                    )
                }

                item {
                    Spacer(modifier = Modifier.size(100.dp))
                }
            }
        }
    }
}

@Composable
fun SingleStructuredResponse(modifier: Modifier = Modifier, genAiMessage: GenAiMessage, text: String = "") {
    when (genAiMessage) {
        is GenAiTextMessage -> {
            if (genAiMessage.loading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Card {
                        Row(
                            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                genAiMessage.query,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Image(
                                painter = painterResource(R.drawable.user_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                                    .drawBehind {
                                        drawCircle(color = Color(245, 245, 245))
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    Card {
                        Text(
                            stringResource(R.string.analysing_response),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            } else {
                genAiMessage.response?.let { b ->
                    OutlinedCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            genAiMessage.query,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )

                        ExpandableRichText(text = b, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }

        is GenAiEntityMessage -> {
            // Show company ui
        }
    }
}

@Preview
@Composable
fun ConversationPreview(modifier: Modifier = Modifier) {
    val messages = listOf(
        GenAiTextMessage(
            query = "I am the best",
            loading = true,
            response = "I am a fan of Manchester United based in Nigeria and also interested in the success of the club in general"
        ),
        GenAiTextMessage(
            query = "What are the latest prediction for netflix stock price?",
            loading = false,
            response = ""
        )
    )
    val conversation =
        StructuredConversation(id = 1, title = "Aak me", messageList = messages.toMutableList())
    GPTInvestorTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            StructuredConversationScreen(
                modifier = Modifier,
                conversation = conversation,
                text = "",
                onNavigateUp = {}
            )
        }
    }
}
