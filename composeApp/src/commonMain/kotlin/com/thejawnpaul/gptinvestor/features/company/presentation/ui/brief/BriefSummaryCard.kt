package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.section_summary
import org.jetbrains.compose.resources.stringResource

@Composable
fun BriefSummaryCard(summary: String, modifier: Modifier = Modifier) {
    BriefCard(modifier = modifier) {
        BriefSectionTitle(text = stringResource(Res.string.section_summary))
        Spacer(Modifier.height(12.dp))
        BriefExpandableText(text = summary)
    }
}
