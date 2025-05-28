package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.SingleCompanyItem
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDiscoveryAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDiscoveryEvent
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDiscoveryState
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.SectorChoiceQuestion

@Composable
fun DiscoverScreen(modifier: Modifier, state: CompanyDiscoveryState, onEvent: (CompanyDiscoveryEvent) -> Unit, onAction: (CompanyDiscoveryAction) -> Unit) {
    // Discover Screen
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                SearchBarCustom(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    query = state.companyView.query,
                    placeHolder = state.sectorView.searchPlaceHolder,
                    onQueryChange = { newQuery ->
                        onEvent(CompanyDiscoveryEvent.SearchQueryChanged(newQuery))
                    },
                    onSearch = {
                        keyboardController?.hide()
                        onEvent(CompanyDiscoveryEvent.PerformSearch)
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // row of sectors
                    SectorChoiceQuestion(
                        possibleAnswers = state.sectorView.sectors,
                        selectedAnswer = state.sectorView.selected,
                        onOptionSelected = {
                            onEvent(CompanyDiscoveryEvent.SelectSector(it))
                        }
                    )
                }

                if (state.companyView.loading) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                if (state.companyView.showError) {
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
                                    onEvent(CompanyDiscoveryEvent.RetryCompanies)
                                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                    Text(text = stringResource(id = R.string.retry))
                                }
                            }
                        }
                    }
                }

                if (state.companyView.showSearchError) {
                    item {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.align(Alignment.Center)) {
                                Image(
                                    painter = painterResource(id = R.drawable.empty),
                                    contentDescription = "Empty",
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
                                    text = stringResource(id = R.string.no_search_result),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                items(
                    items = state.companyView.companies,
                    key = { company -> company.ticker }
                ) { company ->
                    SingleCompanyItem(modifier = Modifier, company = company, onClick = {
                        onAction(CompanyDiscoveryAction.OnNavigateToCompanyDetail(company.ticker))
                    })
                }
            }
        }
    }
}
