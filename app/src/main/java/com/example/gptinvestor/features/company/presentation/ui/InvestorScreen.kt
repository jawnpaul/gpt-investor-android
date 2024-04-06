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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.gptinvestor.R
import com.example.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel

@Composable
fun AIInvestorScreen(modifier: Modifier, viewModel: CompanyViewModel) {
    val company = viewModel.selectedCompany.collectAsState()
    val similarCompanies = viewModel.similarCompanies.collectAsState()
    val comparisonCardVisible = similarCompanies.value.result != null
    val compareCompanies = viewModel.companyComparison.collectAsState()
    val sentimentCardVisible = compareCompanies.value.result != null
    val sentimentAnalysis = viewModel.companySentiment.collectAsState()
    val analystCardVisible = sentimentAnalysis.value.result != null
    val analystRating = viewModel.analystRating.collectAsState()
    val industryCardVisible = analystRating.value.result != null
    val industryRating = viewModel.industryRating.collectAsState()
    val finalRatingCardVisible = industryRating.value.result != null
    val finalRating = viewModel.finalAnalysis.collectAsState()

    Column {
        // Discover similar companies
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
                    Text(text = stringResource(id = R.string.go))
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

        // Compare companies
        AnimatedVisibility(visible = comparisonCardVisible) {
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

        // Sentiment analysis
        AnimatedVisibility(visible = sentimentCardVisible) {
            ElevatedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                val companyName = company.value.company?.name ?: ""
                Text(
                    text = stringResource(id = R.string.sentiment_analysis_insight),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.uncover_prevailing_market,
                        companyName
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )

                if (sentimentAnalysis.value.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (sentimentAnalysis.value.result == null) {
                    Button(
                        onClick = {
                            viewModel.getSentiment()
                        },
                        enabled = !sentimentAnalysis.value.loading,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    ) {
                        Text(text = stringResource(id = R.string.tell_me))
                    }
                } else {
                    Text(
                        text = sentimentAnalysis.value.result!!,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Analyst rating
        AnimatedVisibility(visible = analystCardVisible) {
            ElevatedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.analyst_ratings_and_recommendation),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.leverage_expert_insight
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )

                if (analystRating.value.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (analystRating.value.result == null) {
                    Button(
                        onClick = {
                            viewModel.getAnalystRating()
                        },
                        enabled = !analystRating.value.loading,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    ) {
                        Text(text = stringResource(id = R.string.tell_me))
                    }
                } else {
                    Text(
                        text = analystRating.value.result!!,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Industry rating
        AnimatedVisibility(visible = industryCardVisible) {
            ElevatedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.industry_trends_and_analysis),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.explore_the_competitive_landscape
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )

                if (industryRating.value.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (industryRating.value.result == null) {
                    Button(
                        onClick = {
                            viewModel.getIndustryRating()
                        },
                        enabled = !industryRating.value.loading,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    ) {
                        Text(text = stringResource(id = R.string.go))
                    }
                } else {
                    Text(
                        text = industryRating.value.result!!,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Final analysis
        AnimatedVisibility(visible = finalRatingCardVisible) {
            ElevatedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.comprehensive_analysis_summary),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.synthesize_key_findings
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                )

                if (finalRating.value.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (finalRating.value.result == null) {
                    Button(
                        onClick = {
                            viewModel.getFinalRating()
                        },
                        enabled = !finalRating.value.loading,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    ) {
                        Text(text = stringResource(id = R.string.let_s_go))
                    }
                } else {
                    Text(
                        text = finalRating.value.result!!,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
