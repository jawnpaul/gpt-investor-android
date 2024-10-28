package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.AboutStockCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailDataSource
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailPriceCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailTab
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.ExpandableRichText
import com.thejawnpaul.gptinvestor.features.conversation.data.repository.Suggestion
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
    text: String,
    onClickNews: (url: String) -> Unit,
    onClickSuggestion: (prompt: Suggestion) -> Unit
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
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                        text = text,
                        onClickNews = onClickNews
                    )
                }

                item {
                    if (conversation.suggestedPrompts.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            FollowUpQuestions(
                                entity = null,
                                list = conversation.suggestedPrompts,
                                onClick = onClickSuggestion
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.size(100.dp))
                }
            }
        }
    }
}

@Composable
fun SingleStructuredResponse(
    modifier: Modifier = Modifier,
    genAiMessage: GenAiMessage,
    text: String = "",
    onClickNews: (url: String) -> Unit
) {
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
            Column(modifier = Modifier.fillMaxWidth()) {
                genAiMessage.entity?.let { entity ->

                    // data source
                    CompanyDetailDataSource(
                        list = entity.news.map { it.toPresentation() },
                        source = entity.newsSourcesString
                    )

                    // price card
                    CompanyDetailPriceCard(
                        ticker = entity.ticker,
                        price = entity.price,
                        change = entity.change,
                        imageUrl = entity.imageUrl
                    )

                    // about company card
                    AboutStockCard(
                        companySummary = entity.about,
                        companyName = entity.name
                    )

                    // tabs
                    CompanyDetailTab(
                        company = entity,
                        onClickNews = onClickNews
                    )
                }
            }
        }
    }
}

@Composable
fun FollowUpQuestions(
    modifier: Modifier = Modifier,
    entity: String? = null,
    list: List<Suggestion>,
    onClick: (prompt: Suggestion) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (entity != null) {
            Text(
                stringResource(R.string.related_to, entity),
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            Text(
                stringResource(R.string.related),
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        list.forEach {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(it) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        it.label,
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
    val prompts = listOf(
        Suggestion(
            label = "Netflix stock prices is going " +
                "really high and things are expected tp go higher the more this season",
            query = ""
        ),
        Suggestion(
            label = "Netflix stock prices is going " +
                "really high and things are expected tp go higher the more this season",
            query = ""
        ),
        Suggestion(
            label = "Netflix stock prices is going " +
                "really high and things are expected tp go higher the more this season",
            query = ""
        )
    )
    GPTInvestorTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            FollowUpQuestions(entity = "Netflix", list = prompts) { }
        }
    }
}
