package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ic_search
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchEmptyStateSection(message: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = LocalGPTInvestorColors.current.textColors.secondary50
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = LocalGPTInvestorColors.current.textColors.secondary50,
            textAlign = TextAlign.Center
        )
    }
}

@PreviewLightDark
@Composable
private fun SearchEmptyStateSectionPreview() {
    GPTInvestorTheme {
        SearchEmptyStateSection(
            message = "No results found",
            subtitle = "Try searching for a stock ticker or company name",
            modifier = Modifier.padding(32.dp)
        )
    }
}
