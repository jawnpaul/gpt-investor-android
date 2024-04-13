package com.example.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gptinvestor.core.navigation.Screen
import com.example.gptinvestor.features.company.presentation.viewmodel.CompanyViewModel

@Composable
fun CompanyNewsScreen(modifier: Modifier, navController: NavController, viewModel: CompanyViewModel) {
    val financials = viewModel.companyFinancials.collectAsState()

    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
        financials.value.financialsPresentation?.let { companyFinancialsPresentation ->
            companyFinancialsPresentation.news.forEach { newsPresentation ->

                SingleNewsItem(
                    modifier = Modifier,
                    newsPresentation = newsPresentation,
                    onClick = { link ->
                        viewModel.setUrlToLoad(link)
                        navController.navigate(Screen.WebViewScreen.route)
                    }
                )
            }
        }
    }
}
