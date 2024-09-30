package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.InputBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: CompanyViewModel,
    ticker: String
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val selectedCompany = viewModel.selectedCompany.collectAsState()

    LaunchedEffect(ticker) {
        viewModel.updateTicker(ticker)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }

                Text(
                    text = selectedCompany.value.company?.name ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            if (selectedCompany.value.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            selectedCompany.value.company?.let { company ->
                //data source
                CompanyDetailDataSource(
                    list = company.news.map { it.toPresentation() },
                    source = company.newsSourcesString
                )

                //price card
                CompanyDetailPriceCard(
                    ticker = company.ticker,
                    price = company.price,
                    change = company.change,
                    imageUrl = company.imageUrl
                )

                //about company card
                AboutStockCard(
                    companySummary = company.about,
                    companyName = company.name
                )

                // tabs
                CompanyDetailTab(
                    company = company,
                    onClickNews = {
                        navController.navigate(Screen.WebViewScreen.createRoute(it))
                    }
                )
            }

        }
        InputBar(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.ime.exclude(
                        WindowInsets.navigationBars
                    )
                ),
            input = "",
            contentPadding = PaddingValues(0.dp),
            sendEnabled = false,
            onInputChanged = { input ->

            },
            onSendClick = {
            },
            placeholder = stringResource(
                R.string.ask_anything_about,
                selectedCompany.value.company?.name ?: ""
            ),
            shouldRequestFocus = false
        )
    }
}
