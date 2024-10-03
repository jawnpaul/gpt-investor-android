package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation

@Composable
fun SingleCompanyItem(modifier: Modifier, company: CompanyPresentation, onClick: (String) -> Unit) {

    OutlinedCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = { onClick(company.ticker) }) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    //Text
                    Text(text = company.ticker, style = MaterialTheme.typography.titleLarge)

                    //price
                    StockPriceText(currencySymbol = "$", amount = company.price)

                    //change pill
                    PercentageChangePill(change = company.change, date = company.changeReadableDate)

                }

                AsyncImage(
                    model = company.logo,
                    contentDescription = null,
                    modifier = Modifier,
                    contentScale = ContentScale.Inside
                )

            }
            //About
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = company.summary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
