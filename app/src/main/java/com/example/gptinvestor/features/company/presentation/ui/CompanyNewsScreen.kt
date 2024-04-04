package com.example.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel

@Composable
fun CompanyNewsScreen(modifier: Modifier, viewModel: CompanyViewModel) {
    val financials = viewModel.companyFinancials.collectAsState()

    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
        financials.value.financialsPresentation?.let { companyFinancialsPresentation ->
            companyFinancialsPresentation.news.forEach { newsPresentation ->

                SingleNewsItem(modifier = Modifier, newsPresentation = newsPresentation)
            }
        }
    }
}
