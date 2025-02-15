package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
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
    val currentUser = viewModel.currentUser.collectAsStateWithLifecycle()

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_menu_24),
                        contentDescription = null
                    )
                }

                Text(
                    text = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                if (currentUser.value != null) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                    ) {
                        if (currentUser.value!!.photoUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(currentUser.value!!.photoUrl),
                                contentDescription = null,
                                modifier = Modifier
                            )
                        } else {
                            Box {
                                val firstChar =
                                    currentUser.value!!.displayName?.first().toString().uppercase()
                                Text(firstChar, modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                } else {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(40.dp),
                        enabled = true
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_account_circle_24),
                            contentDescription = null
                        )
                    }
                }
            }

            // Image
            Image(
                painter = painterResource(R.drawable.asset_3_1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )

            // Top Picks
            TopPicks(modifier = Modifier, state = topPicks.value, onClick = {
                navController.navigate(Screen.TopPickDetailScreen.createRoute(it))
            }, onClickRetry = {}, onClickSeeAll = {
                navController.navigate(Screen.AllTopPicksScreen.route)
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
