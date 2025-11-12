package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.trending_down
import gptinvestor.app.generated.resources.trending_up
import org.jetbrains.compose.resources.painterResource

@Composable
fun SingleTopPickItem(
    modifier: Modifier,
    pickPresentation: TopPickPresentation,
    onClick: (String) -> Unit
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    OutlinedCard(
        modifier = modifier.padding(horizontal = 16.dp),
        onClick = { onClick(pickPresentation.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable(
                    interactionSource = null,
                    onClick = { onClick(pickPresentation.id) },
                    indication = null
                )
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
                        model = pickPresentation.imageUrl,
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
                            text = pickPresentation.ticker,
                            style = MaterialTheme.typography.titleMedium
                        )
                        // Text - company name
                        Text(
                            modifier = Modifier,
                            text = pickPresentation.companyName,
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
                            modifier = Modifier.align(Alignment.End),
                            text = "$${pickPresentation.currentPrice.toTwoDecimalPlaces()}",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.End
                        )
                        // Text - percentage change
                        if (pickPresentation.percentageChange < 0) {
                            Row(
                                modifier = Modifier.align(Alignment.End),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    modifier = Modifier.size(12.dp),
                                    painter = painterResource(Res.drawable.trending_down),
                                    contentDescription = null
                                )
                                Text(
                                    text = "${pickPresentation.percentageChange.toTwoDecimalPlaces()}%",
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
                                    painter = painterResource(Res.drawable.trending_up),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(
                                        gptInvestorColors.greenColors.defaultGreen
                                    )
                                )
                                Text(
                                    text = "+${pickPresentation.percentageChange.toTwoDecimalPlaces()}%",
                                    color = gptInvestorColors.greenColors.defaultGreen,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier,
                text = pickPresentation.rationale,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HomeTopPickItem(
    modifier: Modifier,
    pickPresentation: TopPickPresentation,
    onClick: (String) -> Unit
) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    Column(
        modifier = modifier.clickable(
            interactionSource = null,
            onClick = { onClick(pickPresentation.id) },
            indication = null
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape
            ) {
                AsyncImage(
                    model = pickPresentation.imageUrl,
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
                        text = pickPresentation.ticker,
                        style = MaterialTheme.typography.titleMedium
                    )
                    // Text - company name
                    Text(
                        modifier = Modifier,
                        text = pickPresentation.companyName,
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
                        modifier = Modifier.align(Alignment.End),
                        text = "$${pickPresentation.currentPrice.toTwoDecimalPlaces()}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End
                    )
                    // Text - percentage change
                    if (pickPresentation.percentageChange < 0) {
                        Row(
                            modifier = Modifier.align(Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                modifier = Modifier.size(12.dp),
                                painter = painterResource(Res.drawable.trending_down),
                                contentDescription = null
                            )
                            Text(
                                text = "${pickPresentation.percentageChange.toTwoDecimalPlaces()}%",
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
                                painter = painterResource(Res.drawable.trending_up),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    gptInvestorColors.greenColors.defaultGreen
                                )
                            )
                            Text(
                                text = "+${pickPresentation.percentageChange.toTwoDecimalPlaces()}%",
                                color = gptInvestorColors.greenColors.defaultGreen,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier,
            text = pickPresentation.rationale,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun SinglePickPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                val pick = TopPickPresentation(
                    id = "1",
                    ticker = "AAPL",
                    companyName = "Microsoft",
                    rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, oh well it is just blah lorem ipsum ",
                    confidenceScore = 2,
                    metrics = emptyList(),
                    risks = emptyList(),
                    isSaved = false,
                    imageUrl = "",
                    percentageChange = 0.0f,
                    currentPrice = 0.0f
                )

                SingleTopPickItem(modifier = Modifier, pickPresentation = pick) { }
            }
        }
    }
}
