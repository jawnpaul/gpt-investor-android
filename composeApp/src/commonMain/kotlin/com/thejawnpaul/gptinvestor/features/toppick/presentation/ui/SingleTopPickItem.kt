package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun SingleTopPickItem(pickPresentation: TopPickPresentation, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )

    OutlinedCard(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        interactionSource = interactionSource,
        onClick = { onClick(pickPresentation.id) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface
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
                        modifier = Modifier.weight(1f),
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
fun HomeTopPickItem(pickPresentation: TopPickPresentation, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(interactionSource = interactionSource, indication = null, onClick = {
                onClick(pickPresentation.id)
            }),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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

@PreviewLightDark
@Composable
private fun SinglePickPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                val pick = TopPickPresentation(
                    id = "1",
                    ticker = "AAPL",
                    companyName = "Microsoft is the leader of the new leader in a new era",
                    rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, " +
                        "oh well it is just blah lorem ipsum ",
                    confidenceScore = 2,
                    metrics = emptyList(),
                    risks = emptyList(),
                    isSaved = false,
                    imageUrl = "",
                    percentageChange = 0.0f,
                    currentPrice = 0.0f
                )

                SingleTopPickItem(modifier = Modifier, pickPresentation = pick, onClick = {})
                HomeTopPickItem(modifier = Modifier, pickPresentation = pick, onClick = {})
            }
        }
    }
}
