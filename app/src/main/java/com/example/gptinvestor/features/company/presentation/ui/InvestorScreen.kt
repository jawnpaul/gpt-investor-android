package com.example.gptinvestor.features.company.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.gptinvestor.R
import com.example.gptinvestor.features.investor.presentation.viewmodel.HomeViewModel

@Composable
fun AIInvestorScreen(modifier: Modifier, viewModel: HomeViewModel) {
    val similarCompanies = viewModel.similarCompanies.collectAsState()
    val visible = similarCompanies.value.result != null
    val compareCompanies = viewModel.companyComparison.collectAsState()

    Column {
        ElevatedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
            Text(
                text = stringResource(id = R.string.discover_related_companies),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.explore_businesses),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
            )

            if (similarCompanies.value.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (similarCompanies.value.result == null) {
                Button(
                    onClick = {
                        viewModel.getSimilarCompanies()
                    },
                    enabled = !similarCompanies.value.loading,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    Text(text = "Go!")
                }
            } else {
                // show kotlin code
                val codeText = similarCompanies.value.result!!.codeText
                val companies = similarCompanies.value.result!!.companies
                val code = buildAnnotatedString {
                    val spanStyle = SpanStyle(
                        fontFamily = FontFamily.Monospace
                    )

                    withStyle(style = spanStyle) {
                        companies.forEach {
                            append(it)
                            append("\n")
                        }
                    }
                }
                Text(
                    text = code,
                    Modifier
                        .padding(start = 8.dp, top = 8.dp)
                        .fillMaxWidth()
                )
            }
        }

        AnimatedVisibility(visible = visible) {
            ElevatedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.in_depth_company_comparison_analysis),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )
                Text(
                    text = stringResource(id = R.string.gain_valuable_insight),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )

                if (similarCompanies.value.result != null) {
                    CompanyChoiceQuestion(
                        possibleAnswers = similarCompanies.value.result!!.cleaned,
                        selectedAnswer = compareCompanies.value.selectedCompany,
                        onOptionSelected = {
                            viewModel.compareCompanies(it)
                        },
                        enabled = !compareCompanies.value.loading
                    )

                    if (compareCompanies.value.loading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }

                    if (compareCompanies.value.result != null) {
                        Text(
                            text = compareCompanies.value.result!!,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
