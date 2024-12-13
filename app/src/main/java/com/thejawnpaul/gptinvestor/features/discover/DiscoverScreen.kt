package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.SingleCompanyItem
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.SectorChoiceQuestion
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel

@Composable
fun DiscoverScreen(modifier: Modifier = Modifier, navController: NavHostController, homeViewModel: HomeViewModel, companyViewModel: CompanyViewModel) {
    // Discover Screen

    val sectorViewState = homeViewModel.allSector.collectAsState()
    val allCompaniesViewState = homeViewModel.allCompanies.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier) {
            Text(
                text = stringResource(R.string.discover_gpt_investor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // row of sectors
                    SectorChoiceQuestion(
                        possibleAnswers = sectorViewState.value.sectors,
                        selectedAnswer = sectorViewState.value.selected,
                        onOptionSelected = {
                            homeViewModel.selectSector(it)
                        }
                    )
                }

                item {
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }

                if (allCompaniesViewState.value.loading) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                if (allCompaniesViewState.value.showError) {
                    item {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.align(Alignment.Center)) {
                                Image(
                                    painter = painterResource(id = R.drawable.server_down),
                                    contentDescription = "Server down",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 8.dp,
                                            end = 8.dp,
                                            bottom = 8.dp,
                                            top = 8.dp
                                        )
                                )

                                Text(
                                    text = stringResource(id = R.string.something_went_wrong),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.padding(8.dp))
                                OutlinedButton(onClick = {
                                    homeViewModel.getAllCompanies()
                                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                    Text(text = stringResource(id = R.string.retry))
                                }
                            }
                        }
                    }
                }

                items(
                    items = allCompaniesViewState.value.companies,
                    key = { company -> company.ticker }
                ) { company ->
                    SingleCompanyItem(modifier = Modifier, company = company, onClick = {
                        navController.navigate(Screen.CompanyDetailScreen.createRoute(it))
                    })
                }
            }
        }
    }
}
