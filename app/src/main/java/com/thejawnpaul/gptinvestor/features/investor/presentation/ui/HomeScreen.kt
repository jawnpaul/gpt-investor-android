package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavHostController? = null, viewModel: HomeViewModel) {
    // Home Screen

    val trendingStock = viewModel.trendingCompanies.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
        ) {
            // App bar
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            // Image
            Image(
                painter = painterResource(R.drawable.asset_3_1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )

            // Text
            Text(
                text = stringResource(R.string.start_trading_tending_stocks),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            // Trending
            TrendingStockList(
                modifier = Modifier,
                state = trendingStock.value,
                onClick = {},
                onClickRetry = { viewModel.getTrendingCompanies() }
            )
        }

        // Input layout
        ClickableInputBar(
            input = "",
            onInputChanged = {
            },
            onSendClick = {
                // navigate
            },
            onBarClick = {
                // navigate
                // viewModel.doSomething()
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomStart),
            sendEnabled = true
        )
    }
}
