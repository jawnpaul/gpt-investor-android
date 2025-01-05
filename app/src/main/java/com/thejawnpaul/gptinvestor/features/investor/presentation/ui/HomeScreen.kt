package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.TopPicks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavHostController, viewModel: HomeViewModel) {
    // Home Screen

    val trendingStock = viewModel.trendingCompanies.collectAsState()
    val topPicks = viewModel.topPicks.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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

            /*// Text
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
                onClick = {
                    navController.navigate(Screen.CompanyDetailScreen.createRoute(it))
                },
                onClickRetry = { viewModel.getTrendingCompanies() }
            )*/

            // Top Picks
            TopPicks(modifier = Modifier, state = topPicks.value, onClick = {
            }, onClickRetry = {}, onClickSeeAll = {
            })
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomStart),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                border = BorderStroke(
                    width = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                ),
                onClick = {
                    navController.navigate(Screen.ConversationScreen.route)
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        stringResource(R.string.ask_anything_about_stocks),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                    )

                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp),
                        painter = painterResource(R.drawable.send_icon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
