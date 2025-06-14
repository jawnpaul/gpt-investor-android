package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyHeaderPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailHeader
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailTab
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.ExpandableRichText
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.conversation.data.repository.Suggestion
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.theme.bodyChatBody
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StructuredConversationScreen(
    modifier: Modifier,
    conversation: StructuredConversation,
    onNavigateUp: () -> Unit,
    text: String,
    onClickNews: (url: String) -> Unit,
    onClickFeedback: (messageId: Long, status: Int, reason: String?) -> Unit,
    onCopy: (String) -> Unit,
    inputQuery: String,
    onInputQueryChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    companyName: String = "",
    onClickSuggestedPrompt: (String) -> Unit,
    availableModels: List<AvailableModel>,
    selectedModel: AvailableModel,
    onUpgradeModel: () -> Unit,
    onModelChange: (AvailableModel) -> Unit
) {
    val company = conversation.messageList.filterIsInstance<GenAiEntityMessage>().firstOrNull()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            company?.let {
                CompanyDetailHeader(
                    modifier = Modifier.fillMaxWidth(),
                    onNavigateUp = onNavigateUp,
                    companyHeader = CompanyHeaderPresentation(
                        companyTicker = it.entity?.ticker ?: "",
                        companyName = it.entity?.name ?: "",
                        companyLogo = it.entity?.imageUrl ?: "",
                        price = it.entity?.price ?: 0f,
                        percentageChange = it.entity?.change ?: 0f
                    )
                )
            } ?: Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        bottomBar = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (conversation.suggestedPrompts.isNotEmpty()) {
                    FollowUpQuestions(
                        modifier = Modifier,
                        entity = null,
                        list = conversation.suggestedPrompts,
                        onClick = { prompt ->
                            onClickSuggestedPrompt(prompt.query)
                        }
                    )
                }

                QuestionInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(
                            insets = WindowInsets.ime
                        ),
                    onSendClicked = {
                        keyboardController?.hide()
                        onSendClick()
                    },
                    hint = stringResource(
                        R.string.ask_anything_about,
                        companyName
                    ),
                    onTextChange = { input ->
                        onInputQueryChanged(input)
                    },
                    text = inputQuery,
                    availableModels = availableModels,
                    selectedModel = selectedModel,
                    onModelChange = {
                        if (it.canUpgrade) {
                            /*onEvent(
                                HomeEvent.UpgradeModel(
                                    showBottomSheet = true,
                                    modelId = it.modelId
                                )
                            )
                            return@QuestionInput*/
                        }
                        onModelChange(it)
                    }
                )
            }
        }
    ) { innerPadding ->

        var showBottomSheet by remember { mutableStateOf(false) }

        if (showBottomSheet) {
            GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
                showBottomSheet = false
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sources),
                        style = MaterialTheme.typography.labelMedium
                    )

                    company?.entity?.news?.map { it.toPresentation() }?.let { news ->
                        news.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp
                                )
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = item.publisher,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            if (index != news.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()

        ) {
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
                            onClickNews = onClickNews,
                            onClickFeedback = onClickFeedback,
                            onCopy = onCopy,
                            onClickSource = {
                                showBottomSheet = true
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.size(100.dp))
                    }
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
    onClickNews: (url: String) -> Unit,
    onClickFeedback: (messageId: Long, status: Int, reason: String?) -> Unit,
    onCopy: (text: String) -> Unit,
    onClickSource: () -> Unit
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

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
                genAiMessage.response?.let { modelResponse ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val showDislikeReasons = remember { mutableStateOf(false) }
                        val feedBackState = remember { mutableStateOf(genAiMessage.feedbackStatus) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Spacer(modifier = Modifier.weight(0.1f))
                            Surface(
                                modifier = Modifier.weight(0.9f),
                                shape = RoundedCornerShape(corner = CornerSize(12.dp))
                            ) {
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = genAiMessage.query,
                                    style = MaterialTheme.typography.bodyChatBody,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Start)
                                .padding(bottom = 16.dp)
                        ) {
                            ExpandableRichText(
                                text = modelResponse,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            val dislikeReasons =
                                listOf(
                                    R.string.too_complex,
                                    R.string.inaccurate,
                                    R.string.not_actionable,
                                    R.string.others
                                )

                            if (showDislikeReasons.value) {
                                // list of dislike reasons
                                LazyRow(
                                    modifier = Modifier.padding(top = 8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(dislikeReasons) { reason ->
                                        val stringReason = stringResource(reason)
                                        Surface(
                                            modifier = modifier,
                                            onClick = {
                                                onClickFeedback(
                                                    genAiMessage.id,
                                                    -1,
                                                    stringReason
                                                )
                                                feedBackState.value = -1
                                                showDislikeReasons.value = false
                                            },
                                            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outlineVariant
                                            )
                                        ) {
                                            Text(
                                                text = stringReason,
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                                style = MaterialTheme.typography.labelMedium,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                // show it for 15 seconds
                                LaunchedEffect(Unit) {
                                    delay(15000L)
                                    showDislikeReasons.value = false
                                }
                            } else {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // copy
                                    IconButton(
                                        modifier = Modifier.size(32.dp),
                                        onClick = {
                                            onCopy(modelResponse)
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_copy),
                                            contentDescription = stringResource(R.string.copy)
                                        )
                                    }

                                    when (feedBackState.value) {
                                        1 -> {
                                            // like chosen
                                            IconButton(
                                                modifier = Modifier.size(32.dp),
                                                onClick = {
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_like_filled),
                                                    contentDescription = stringResource(R.string.like_chosen)
                                                )
                                            }
                                        }

                                        -1 -> {
                                            // dislike chosen
                                            IconButton(modifier = Modifier.size(32.dp), onClick = {
                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_dislike_filled),
                                                    contentDescription = stringResource(R.string.dislike_chosen)
                                                )
                                            }
                                        }

                                        else -> {
                                            // none chosen

                                            // like
                                            IconButton(
                                                modifier = Modifier.size(32.dp),
                                                onClick = {
                                                    feedBackState.value = 1
                                                    onClickFeedback(genAiMessage.id, 1, null)
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_like),
                                                    contentDescription = stringResource(R.string.like)
                                                )
                                            }

                                            // dislike
                                            IconButton(
                                                modifier = Modifier.size(32.dp),
                                                onClick = {
                                                    showDislikeReasons.value = true
                                                    feedBackState.value = -1
                                                    onClickFeedback(genAiMessage.id, -1, null)
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_dislike),
                                                    contentDescription = stringResource(R.string.dislike)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        is GenAiEntityMessage -> {
            // Show company ui
            Column(modifier = Modifier.fillMaxWidth()) {
                genAiMessage.entity?.let { entity ->

                    // tabs
                    CompanyDetailTab(
                        modifier = Modifier.fillMaxWidth(),
                        company = entity,
                        onClickNews = onClickNews,
                        onClickSources = onClickSource
                    )
                }
            }
        }
    }
}

@Composable
fun FollowUpQuestions(modifier: Modifier, entity: String? = null, list: List<Suggestion>, onClick: (prompt: Suggestion) -> Unit) {
    if (list.isNotEmpty()) {
        LazyRow(
            modifier = Modifier,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = list) { suggestion ->
                Surface(
                    modifier = modifier,
                    onClick = {
                        onClick(suggestion)
                    },
                    shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Text(
                        text = suggestion.label,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
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
            response = "I am a fan of Manchester United based in Nigeria and also interested in the success of the club in general",
            feedbackStatus = 0
        ),
        GenAiTextMessage(
            query = "What are the latest prediction for netflix stock price?",
            loading = false,
            response = "",
            feedbackStatus = 1
        )
    )
    val conversation =
        StructuredConversation(id = 1, title = "Aak me", messageList = messages.toMutableList())
    val prompts = listOf(
        Suggestion(
            label = "Netflix stock prices is going ",
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
            FollowUpQuestions(modifier = Modifier, entity = "Netflix", list = prompts) { }
        }
    }
}
