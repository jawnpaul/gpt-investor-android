package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun SingleCompanyItem(modifier: Modifier, company: CompanyPresentation, onClick: (String) -> Unit) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    OutlinedCard(
        modifier = modifier.padding(horizontal = 16.dp),
        onClick = { onClick(company.ticker) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape
                ) {
                    AsyncImage(
                        model = company.logo,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Text - company ticker
                        Text(
                            text = company.ticker,
                            style = MaterialTheme.typography.titleMedium
                        )
                        // Text - company name
                        Text(
                            modifier = Modifier,
                            text = company.name,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = gptInvestorColors.textColors.secondary50
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Text - company price
                        Text(
                            text = "$${company.price.toTwoDecimalPlaces()}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        // Text - percentage change
                        if (company.priceChange.change < 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    modifier = Modifier.size(12.dp),
                                    painter = painterResource(R.drawable.trending_down),
                                    contentDescription = null
                                )
                                Text(
                                    text = "${company.priceChange.change.toTwoDecimalPlaces()}%",
                                    color = Color(212, 38, 32),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    modifier = Modifier.size(12.dp),
                                    painter = painterResource(R.drawable.trending_up),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(
                                        gptInvestorColors.greenColors.defaultGreen
                                    )
                                )
                                Text(
                                    text = "+${company.priceChange.change.toTwoDecimalPlaces()}%",
                                    color = gptInvestorColors.greenColors.defaultGreen,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About
            Text(
                modifier = Modifier,
                text = company.summary,
                maxLines = 3,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
