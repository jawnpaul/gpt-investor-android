package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.SingleTopPickItem
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun DiscoverScreen(modifier: Modifier, state: CompanyDiscoveryState, onEvent: (CompanyDiscoveryEvent) -> Unit, onAction: (CompanyDiscoveryAction) -> Unit) {
    // Discover Screen
    val keyboardController = LocalSoftwareKeyboardController.current
    val gptInvestorColors = LocalGPTInvestorColors.current

    Scaffold(
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onEvent(CompanyDiscoveryEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }

                    if (state.searchMode) {
                        SearchBarCustom(
                            modifier = Modifier
                                .fillMaxWidth(),
                            query = state.companyView.query,
                            placeHolder = state.sectorView.searchPlaceHolder,
                            onQueryChange = { newQuery ->
                                onEvent(CompanyDiscoveryEvent.SearchQueryChanged(newQuery))
                            },
                            onSearch = {
                                keyboardController?.hide()
                                onEvent(CompanyDiscoveryEvent.PerformSearch)
                            },
                            onClose = {
                                keyboardController?.hide()
                                onEvent(CompanyDiscoveryEvent.ToggleSearchMode(false))
                            }
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.discover),
                                style = MaterialTheme.typography.titleLarge
                            )

                            Surface(
                                modifier = Modifier,
                                shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                                border = BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.outlineVariant
                                ),
                                onClick = {
                                    onEvent(CompanyDiscoveryEvent.ToggleSearchMode(true))
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 12.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        painter = painterResource(R.drawable.ic_search),
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(R.string.search),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = gptInvestorColors.textColors.secondary50
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()
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
                    .fillMaxSize()
                    .padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
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

                if (state.showTopPicks) {
                    items(
                        items = state.topPicks,
                        key = { topPickPresentation -> topPickPresentation.id }
                    ) { pickPresentation ->
                        SingleTopPickItem(
                            modifier = Modifier,
                            pickPresentation = pickPresentation,
                            onClick = { topPickId ->
                                onAction(
                                    CompanyDiscoveryAction.OnGoToPickDetail(
                                        id = topPickId
                                    )
                                )
                            }
                        )
                    }
                } else {
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
}
