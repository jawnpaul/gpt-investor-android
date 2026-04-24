package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ask_anything_about
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyHeaderPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailEvent
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyDetailDefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.StructuredConversationScreen
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.WaitlistBottomSheetContent
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.sources
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    state: SingleCompanyView,
    onEvent: (CompanyDetailEvent) -> Unit,
    onAction: (CompanyDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            state.conversation.let { conversation ->
                when (conversation) {
                    is CompanyDetailDefaultConversation -> {
                        val keyboardController = LocalSoftwareKeyboardController.current
                        Scaffold(
                            topBar = {
                                Column {
                                    CompanyDetailHeader(
                                        modifier = Modifier.fillMaxWidth(),
                                        onNavigateUp = {
                                            onAction(CompanyDetailAction.OnGoBack)
                                        },
                                        companyHeader = state.header
                                    )
                                    if (state.isGuestSession) {
                                        TopGuestLabel(modifier = Modifier.fillMaxWidth(), onClick = {
                                            onEvent(CompanyDetailEvent.SignUpClicked)
                                        })
                                    }
                                }
                            },
                            bottomBar = {
                                QuestionInput(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .windowInsetsPadding(
                                            insets = WindowInsets.ime
                                        ),
                                    onSendClick = {
                                        keyboardController?.hide()
                                        onEvent(CompanyDetailEvent.SendClick)
                                    },
                                    hint = stringResource(
                                        Res.string.ask_anything_about,
                                        state.companyName
                                    ),
                                    onTextChange = { input ->
                                        onEvent(CompanyDetailEvent.QueryInputChanged(input))
                                    },
                                    text = state.inputQuery,
                                    availableModels = state.availableModels,
                                    selectedModel = state.selectedModel,
                                    onModelChange = {
                                        if (it.canUpgrade) {
                                            onEvent(
                                                CompanyDetailEvent.UpgradeModel(
                                                    showBottomSheet = true,
                                                    modelId = it.modelId
                                                )
                                            )
                                            return@QuestionInput
                                        }
                                        onEvent(CompanyDetailEvent.ModelChange(model = it))
                                    }
                                )
                            }
                        ) { innerPadding ->

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
                                            text = stringResource(Res.string.sources),
                                            style = MaterialTheme.typography.labelMedium
                                        )

                                        conversation.response?.news?.map { it.toPresentation() }
                                            ?.let { news ->
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

                            if (state.showWaitListBottomSheet) {
                                GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
                                    onEvent(
                                        CompanyDetailEvent.UpgradeModel(showBottomSheet = false)
                                    )
                                }) {
                                    WaitlistBottomSheetContent(
                                        modifier = Modifier,
                                        options = state.waitlistAvailableOptions,
                                        selectedOptions = state.selectedWaitlistOptions,
                                        onSelectOption = {
                                            onEvent(CompanyDetailEvent.SelectWaitlistOption(it))
                                        },
                                        onJoinWaitList = {
                                            onEvent(CompanyDetailEvent.JoinWaitList)
                                        },
                                        onDismiss = {
                                            onEvent(
                                                CompanyDetailEvent.UpgradeModel(
                                                    showBottomSheet = false
                                                )
                                            )
                                        }
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                if (state.loading) {
                                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                }

                                conversation.response?.let { company ->
                                    // tabs
                                    CompanyDetailTab(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        company = company,
                                        onClickNews = {
                                            onAction(
                                                CompanyDetailAction.OnNavigateToWebView(url = it)
                                            )
                                        },
                                        onClickSources = {
                                            showBottomSheet = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is StructuredConversation -> {
                        StructuredConversationScreen(
                            modifier = Modifier,
                            conversation = conversation,
                            onNavigateUp = {
                                onAction(CompanyDetailAction.OnGoBack)
                            },
                            text = state.genText,
                            onClickNews = {
                                onAction(CompanyDetailAction.OnNavigateToWebView(url = it))
                            },
                            onClickFeedback = { messageId, status, reason ->
                                onEvent(
                                    CompanyDetailEvent.SendFeedback(
                                        messageId = messageId,
                                        status = status,
                                        reason = reason
                                    )
                                )
                            },
                            onCopy = {
                                onEvent(CompanyDetailEvent.CopyToClipboard(it))
                            },
                            inputQuery = state.inputQuery,
                            onInputQueryChange = { input ->
                                onEvent(CompanyDetailEvent.QueryInputChanged(input))
                            },
                            onSendClick = {
                                onEvent(CompanyDetailEvent.SendClick)
                            },
                            companyName = state.companyName,
                            onClickSuggestedPrompt = {
                                onEvent(CompanyDetailEvent.SuggestedPromptClicked(it))
                            },
                            availableModels = state.availableModels,
                            selectedModel = state.selectedModel,
                            onModelChange = { onEvent(CompanyDetailEvent.ModelChange(it)) },
                            onUpgradeModel = { showBottomSheet, modelId ->
                                onEvent(CompanyDetailEvent.UpgradeModel(showBottomSheet, modelId))
                            },
                            onSignUpClick = {
                                onEvent(CompanyDetailEvent.SignUpClicked)
                            }
                        )
                    }

                    else -> {
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompanyDetailScreenPreview() {
    GPTInvestorTheme {
        CompanyDetailScreen(
            modifier = Modifier.fillMaxSize(),
            state = SingleCompanyView(
                companyName = "Apple Inc.",
                header = CompanyHeaderPresentation(
                    companyTicker = "AAPL",
                    companyName = "Apple Inc.",
                    price = 175.43f,
                    percentageChange = 1.2f,
                    companyLogo = ""
                ),
                conversation = CompanyDetailDefaultConversation(
                    id = 1L,
                    response = CompanyDetailRemoteResponse(
                        ticker = "AAPL",
                        name = "Apple Inc.",
                        about =
                        "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets" +
                            ", wearables, and accessories worldwide.",
                        price = 175.43f,
                        change = 1.2f,
                        marketCap = 2800000000000L,
                        peRatio = 28.5f,
                        revenue = 383000000000L,
                        news = emptyList()
                    )
                )
            ),
            onEvent = {},
            onAction = {}
        )
    }
}
