package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.search.domain.model.ChipItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipListSection(chips: List<ChipItem>, onChipClick: (String) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            SuggestionChip(
                onClick = { onChipClick(chip.key) },
                label = {
                    Text(
                        text = chip.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}
