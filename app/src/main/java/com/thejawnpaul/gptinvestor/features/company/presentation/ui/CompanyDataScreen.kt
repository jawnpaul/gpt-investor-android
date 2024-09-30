package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.ui.Table
import com.halilibo.richtext.ui.material3.RichText
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyFinancialsPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel

@Composable
fun CompanyDataScreen(modifier: Modifier, viewModel: CompanyViewModel) {
    val company = viewModel.selectedCompany.collectAsState()
    val financials = viewModel.companyFinancials.collectAsState()

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        ElevatedCard(modifier = Modifier.padding(bottom = 16.dp)) {
            CardContent(
                title = stringResource(id = R.string.about),
                content = company.value.company?.about ?: ""
            )
        }

        ElevatedCard {
            CardNumbers(
                title = stringResource(id = R.string.latest_financials),
                financials = financials.value.financialsPresentation
            )
        }
    }
}

@Composable
private fun CardContent(title: String, content: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (expanded) {
                Text(
                    text = (content)
                )
            }
        }

        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}

@Composable
private fun CardNumbers(title: String, financials: CompanyFinancialsPresentation?) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) {
                            stringResource(R.string.show_less)
                        } else {
                            stringResource(R.string.show_more)
                        }
                    )
                }
            }
            if (expanded) {
                if (financials != null) {
                    RichText {
                        Table(modifier = Modifier.fillMaxWidth()) {
                            row {
                                cell {
                                    Text(
                                        text = stringResource(id = R.string.open),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                cell { Text(financials.open) }
                            }

                            row {
                                cell {
                                    Text(
                                        text = stringResource(id = R.string.high),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                cell { Text(financials.high) }
                            }

                            row {
                                cell {
                                    Text(
                                        text = stringResource(id = R.string.low),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                cell { Text(financials.low) }
                            }

                            row {
                                cell {
                                    Text(
                                        text = stringResource(id = R.string.close),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                cell { Text(financials.close) }
                            }
                            row {
                                cell {
                                    Text(
                                        text = stringResource(id = R.string.volume),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                cell { Text(financials.volume) }
                            }
                            row {
                                cell {
                                    Text(
                                        text = stringResource(id = R.string.market_cap),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                cell { Text(financials.marketCap) }
                            }
                        }
                        /*Column {
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = stringResource(id = R.string.open),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = financials.open)
                            }

                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = stringResource(id = R.string.high),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = financials.high)
                            }

                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = stringResource(id = R.string.low),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = financials.low)
                            }

                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = stringResource(id = R.string.close),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = financials.close)
                            }
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = stringResource(id = R.string.volume),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = financials.volume)
                            }
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = stringResource(id = R.string.market_cap),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = financials.marketCap)
                            }
                        }*/
                    }
                }
            }
        }
    }
}
