package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyDetailDefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.StructuredConversationScreen
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.InputBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(modifier: Modifier, navController: NavController, viewModel: CompanyViewModel, ticker: String) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val selectedCompany = viewModel.selectedCompany.collectAsStateWithLifecycle()
    val genText = viewModel.genText.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(ticker) {
        viewModel.updateTicker(ticker)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            selectedCompany.value.conversation.let { conversation ->
                when (conversation) {
                    is CompanyDetailDefaultConversation -> {
                        Scaffold(
                            topBar = {
                                CompanyDetailHeader(
                                    modifier = Modifier.fillMaxWidth(),
                                    onNavigateUp = {
                                        navController.navigateUp()
                                    },
                                    companyHeader = selectedCompany.value.header
                                )
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

                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                if (selectedCompany.value.loading) {
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
                                            navController.navigate(
                                                Screen.WebViewScreen.createRoute(
                                                    it
                                                )
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
                            onNavigateUp = { navController.navigateUp() },
                            text = genText.value,
                            onClickNews = {
                                navController.navigate(Screen.WebViewScreen.createRoute(it))
                            },
                            onClickFeedback = { messageId, status, reason ->
                            },
                            onCopy = {
                            }
                        )
                    }

                    else -> {
                    }
                }
            }
        }
        InputBar(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.ime
                )
                .navigationBarsPadding(),
            input = selectedCompany.value.inputQuery,
            contentPadding = PaddingValues(0.dp),
            sendEnabled = selectedCompany.value.enableSend,
            onInputChanged = { input ->
                viewModel.getQuery(input)
            },
            onSendClick = {
                keyboardController?.hide()
                viewModel.getInputResponse()
            },
            placeholder = stringResource(
                R.string.ask_anything_about,
                selectedCompany.value.companyName
            ),
            shouldRequestFocus = false
        )
    }
}
