package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.ui.theme.GPTInvestorTheme


@Composable
fun SectorChoiceQuestion(
    possibleAnswers: List<SectorInput>,
    selectedAnswer: SectorInput?,
    onOptionSelected: (SectorInput) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = possibleAnswers) {
            SingleSectorChoice(
                modifier = Modifier,
                input = it,
                selected = it == selectedAnswer,
                onOptionSelected = { onOptionSelected(it) }
            )
        }
    }
}


@Composable
fun SingleSectorChoice(
    input: SectorInput,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (input) {
        is SectorInput.AllSector -> {
            // Text(text = "All")
            FilterChip(
                modifier = Modifier,
                selected = selected,
                onClick = onOptionSelected,
                label = { Text(text = "All") })
        }

        is SectorInput.CustomSector -> {
            // Text(text = input.sectorName)
            FilterChip(
                modifier = Modifier,
                selected = selected,
                onClick = onOptionSelected,
                label = { Text(text = input.sectorName) })
        }
    }

}

@Preview
@Composable
fun SectorPreview() {
    GPTInvestorTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            var selectedAnswer by remember { mutableStateOf<SectorInput?>(null) }
            val possibleAnswers = listOf(
                SectorInput.AllSector,
                SectorInput.CustomSector("Technology", ""),
                SectorInput.CustomSector("Manufacturing", ""),
                SectorInput.CustomSector("Sports", ""),
                SectorInput.CustomSector("Security", ""),
                SectorInput.CustomSector("Fashion", "")
            )

            SectorChoiceQuestion(
                possibleAnswers = possibleAnswers,
                selectedAnswer = selectedAnswer,
                onOptionSelected = {
                    selectedAnswer = it
                }
            )
        }
    }
}
