package com.example.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gptinvestor.R
import com.example.gptinvestor.core.navigation.Screen
import com.example.gptinvestor.features.company.presentation.ui.SingleCompanyItem
import com.example.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavHostController, viewModel: HomeViewModel) {
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
        val sectorViewState = viewModel.allSector.collectAsState()
        val allCompaniesViewState = viewModel.allCompanies.collectAsState()

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
                        viewModel.selectSector(it)
                    }
                )
            }

            items(
                items = allCompaniesViewState.value.companies,
                key = { company -> company.ticker }
            ) { company ->
                SingleCompanyItem(modifier = Modifier, company = company, onClick = {
                    viewModel.getCompany(it)
                    navController.navigate(Screen.CompanyDetailScreen.route)
                })
            }
        }
    }

    /*var showLandingScreen by remember { mutableStateOf(true) }
    if (showLandingScreen) {
        LandingScreen(onTimeout = { showLandingScreen = false })
    } else {
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
            val sectorViewState = viewModel.allSector.collectAsState()
            val allCompaniesViewState = viewModel.allCompanies.collectAsState()

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
                            viewModel.selectSector(it)
                        }
                    )
                }

                items(
                    items = allCompaniesViewState.value.companies,
                    key = { company -> company.ticker }
                ) { company ->
                    SingleCompanyItem(modifier = Modifier, company = company, onClick = {
                        viewModel.getCompany(it)
                        navController.navigate(Screen.CompanyDetailScreen.route)
                    })
                }
            }
        }
    }*/
}
