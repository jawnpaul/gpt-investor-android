package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun BriefChip(text: String, tone: BriefTone, modifier: Modifier = Modifier) {
    val colors = LocalGPTInvestorColors.current
    val (bg, fg) = when (tone) {
        BriefTone.Positive -> colors.greenColors.allGreen10 to colors.greenColors.allGreen
        BriefTone.Negative -> colors.redColors.allRed.copy(alpha = 0.12f) to colors.redColors.allRed
        BriefTone.Neutral -> MaterialTheme.colorScheme.background to MaterialTheme.colorScheme.onBackground
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        modifier = modifier
            .background(color = bg, shape = RoundedCornerShape(percent = 50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
fun BriefSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = LocalGPTInvestorColors.current.textColors.secondary50,
        modifier = modifier
    )
}

@Composable
internal fun briefCardBorderColor(): Color = LocalGPTInvestorColors.current.utilColors.borderBright10

@PreviewLightDark
@Composable
private fun BriefChipPreview() {
    GPTInvestorTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BriefChip(text = "Positive Tone", tone = BriefTone.Positive)
                BriefChip(text = "Neutral Tone", tone = BriefTone.Neutral)
                BriefChip(text = "Negative Tone", tone = BriefTone.Negative)

                BriefSectionTitle(text = "Section Title")
            }
        }
    }
}
