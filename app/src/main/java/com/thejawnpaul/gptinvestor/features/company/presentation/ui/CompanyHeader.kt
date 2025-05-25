package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyHeaderPresentation
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun CompanyDetailHeader(modifier: Modifier, onNavigateUp: () -> Unit, companyHeader: CompanyHeaderPresentation) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape
            ) {
                AsyncImage(
                    model = companyHeader.companyLogo,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = companyHeader.companyTicker,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    modifier = Modifier,
                    text = companyHeader.companyName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = gptInvestorColors.textColors.secondary50
                )
            }
        }

        Row {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Text - company price
                Text(
                    text = "$${companyHeader.price.toTwoDecimalPlaces()}",
                    style = MaterialTheme.typography.bodyLarge
                )
                // Text - percentage change
                if (companyHeader.percentageChange < 0) {
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(R.drawable.trending_down),
                            contentDescription = null
                        )
                        Text(
                            text = "${companyHeader.percentageChange.toTwoDecimalPlaces()}%",
                            color = Color(212, 38, 32),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.align(Alignment.End),
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
                            text = "+${companyHeader.percentageChange.toTwoDecimalPlaces()}%",
                            color = gptInvestorColors.greenColors.defaultGreen,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CompanyHeaderPreview() {
    GPTInvestorTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            CompanyDetailHeader(
                modifier = Modifier,
                onNavigateUp = {},
                companyHeader = CompanyHeaderPresentation(percentageChange = -10f, price = 145.05f)
            )
        }
    }
}
