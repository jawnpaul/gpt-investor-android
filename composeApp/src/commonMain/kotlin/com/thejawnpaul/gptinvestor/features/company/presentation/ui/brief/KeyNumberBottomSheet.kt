package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.key_dividend_yield
import com.thejawnpaul.gptinvestor.key_pe_ratio
import com.thejawnpaul.gptinvestor.key_revenue_growth
import com.thejawnpaul.gptinvestor.market_cap
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun KeyNumberBottomSheet(keyNumber: KeyNumber, modifier: Modifier = Modifier) {
    val colors = LocalGPTInvestorColors.current
    val (bg, fg) = when (keyNumber.tone) {
        BriefTone.Positive -> colors.greenColors.allGreen.copy(alpha = 0.12f) to colors.greenColors.allGreen
        BriefTone.Negative -> colors.redColors.allRed.copy(alpha = 0.12f) to colors.redColors.allRed
        BriefTone.Neutral -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = bg,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(keyNumber.key.labelRes()),
                style = MaterialTheme.typography.bodyMedium,
                color = fg
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = keyNumber.value,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = fg
            )
        }

        if (!keyNumber.insight.isNullOrBlank()) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = keyNumber.insight,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

internal fun KeyNumberType.labelRes(): StringResource = when (this) {
    KeyNumberType.MarketCap -> Res.string.market_cap
    KeyNumberType.PeRatio -> Res.string.key_pe_ratio
    KeyNumberType.RevenueGrowth -> Res.string.key_revenue_growth
    KeyNumberType.DividendYield -> Res.string.key_dividend_yield
}

@PreviewLightDark
@Composable
private fun KeyNumberBottomSheetPreview() {
    GPTInvestorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                KeyNumberBottomSheet(
                    keyNumber = KeyNumber(
                        key = KeyNumberType.PeRatio,
                        value = "345",
                        insight = "The price looks very expensive compared with what the company earns right now.",
                        tone = BriefTone.Negative
                    )
                )
            }
        }
    }
}
