package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.digest.presentation.formatPriceChange
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

/** The day's price move, kept separate from sentiment so the two never contradict each other. */
@Composable
internal fun PriceChangeChip(percent: Double, modifier: Modifier = Modifier) {
    val customColors = LocalGPTInvestorColors.current
    val (background, content) = when {
        percent >= 0.05 -> customColors.greenColors.allGreen10 to customColors.greenColors.allGreen
        percent <= -0.05 -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
        else -> customColors.utilColors.allDark2.copy(alpha = 0.08f) to customColors.textColors.secondary50
    }
    Surface(color = background, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Text(
            text = formatPriceChange(percent),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = content,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
