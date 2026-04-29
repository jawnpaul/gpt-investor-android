package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.today
import org.jetbrains.compose.resources.stringResource

@Composable
fun CompanyBriefHeader(brief: CompanyBrief, modifier: Modifier = Modifier) {
    val colors = LocalGPTInvestorColors.current
    val secondary = colors.textColors.secondary50
    val isPositive = brief.change >= 0f
    val changeColor = if (isPositive) colors.greenColors.defaultGreen else colors.redColors.allRed
    val changeText = buildString {
        if (isPositive) append("+")
        append(brief.change.toTwoDecimalPlaces())
        append("%")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape
        ) {
            AsyncImage(
                model = brief.logoUrl,
                modifier = Modifier.fillMaxSize(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = brief.ticker,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = brief.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$${brief.price.toTwoDecimalPlaces()}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = changeText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = changeColor
                )
                Text(
                    text = stringResource(Res.string.today),
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondary
                )
            }
        }
    }
}
