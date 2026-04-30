package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.section_key_numbers
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun KeyNumbersCard(keyNumbers: List<KeyNumber>, modifier: Modifier = Modifier) {
    var selectedKeyNumber by remember { mutableStateOf<KeyNumber?>(null) }

    BriefCard(modifier = modifier) {
        BriefSectionTitle(text = stringResource(Res.string.section_key_numbers))
        Spacer(Modifier.height(12.dp))
        keyNumbers.forEachIndexed { index, item ->
            KeyNumberRow(
                item = item,
                onClick = if (!item.insight.isNullOrBlank()) {
                    { selectedKeyNumber = item }
                } else {
                    null
                }
            )
            if (index != keyNumbers.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = briefCardBorderColor()
                )
            }
        }
    }

    selectedKeyNumber?.let { keyNumber ->
        GptInvestorBottomSheet(onDismiss = { selectedKeyNumber = null }) {
            KeyNumberBottomSheet(keyNumber = keyNumber)
        }
    }
}

@Composable
private fun KeyNumberRow(item: KeyNumber, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    val secondary = LocalGPTInvestorColors.current.textColors.secondary50

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(0.3f)) {
            Text(
                text = stringResource(item.key.labelRes()),
                style = MaterialTheme.typography.bodySmall,
                color = secondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        if (!item.insight.isNullOrBlank()) {
            Box(modifier = Modifier.weight(0.7f)) {
                BriefChip(modifier = Modifier, text = item.insight, tone = item.tone)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun KeyNumbersCardPreview() {
    GPTInvestorTheme {
        Surface {
            KeyNumbersCard(
                modifier = Modifier.padding(16.dp),
                keyNumbers = listOf(
                    KeyNumber(
                        key = KeyNumberType.MarketCap,
                        value = "$2.83T",
                        insight = "Among the largest in the world",
                        tone = BriefTone.Neutral
                    ),
                    KeyNumber(
                        key = KeyNumberType.PeRatio,
                        value = "29.4",
                        insight = "The price looks very expensive compared with what the company earns",
                        tone = BriefTone.Neutral
                    ),
                    KeyNumber(
                        key = KeyNumberType.RevenueGrowth,
                        value = "+4.2%",
                        insight = "Growing steadily",
                        tone = BriefTone.Positive
                    ),
                    KeyNumber(
                        key = KeyNumberType.DividendYield,
                        value = "0.51%",
                        insight = "Small but reliable",
                        tone = BriefTone.Neutral
                    )
                )
            )
        }
    }
}
