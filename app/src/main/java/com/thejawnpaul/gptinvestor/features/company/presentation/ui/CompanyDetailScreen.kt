package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
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

        Column(
            modifier = Modifier
                .padding(innerPadding)
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
                            Column(
                                modifier = Modifier
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
                                            .fillMaxWidth().padding(top = 16.dp),
                                        company = company,
                                        onClickNews = {
                                            navController.navigate(
                                                Screen.WebViewScreen.createRoute(
                                                    it
                                                )
                                            )
                                        }
                                    )
                                    // about company card
                                    AboutStockCard(
                                        companySummary = company.about,
                                        companyName = company.name
                                    )
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
}
