package com.example.gptinvestor.features.investor.presentation.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gptinvestor.R
import com.example.gptinvestor.core.navigation.Screen
import com.example.gptinvestor.features.company.presentation.ui.SingleCompanyItem
import com.example.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel
import com.example.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavHostController, homeViewModel: HomeViewModel, companyViewModel: CompanyViewModel) {
    // Home Screen
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = {
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        val sectorViewState = homeViewModel.allSector.collectAsState()
        val allCompaniesViewState = homeViewModel.allCompanies.collectAsState()

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 8.dp),
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
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 8.dp)
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
                    companyViewModel.getCompany(it)
                    navController.navigate(Screen.CompanyDetailScreen.route)
                })
            }
        }
    }
}
