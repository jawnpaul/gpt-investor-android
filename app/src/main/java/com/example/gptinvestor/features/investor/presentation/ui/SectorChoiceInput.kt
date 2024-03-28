package com.example.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gptinvestor.features.company.domain.model.SectorInput
import com.example.gptinvestor.ui.theme.GPTInvestorTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SectorChoiceQuestion(
    possibleAnswers: List<SectorInput>,
    selectedAnswer: SectorInput?,
    onOptionSelected: (SectorInput) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
    ) {
        possibleAnswers.forEach {
            val selected = it == selectedAnswer
            SingleSectorChoice(
                modifier = Modifier.padding(horizontal = 4.dp),
                input = it,
                selected = selected,
                onOptionSelected = { onOptionSelected(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleSectorChoice(input: SectorInput, selected: Boolean, onOptionSelected: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = if (selected) {
            MaterialTheme.colorScheme.background
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected,
                onClick = onOptionSelected,
                role = Role.RadioButton
            )

    ) {
        when (input) {
            is SectorInput.AllSector -> {
                // Text(text = "All")
                FilterChip(selected = selected, onClick = onOptionSelected, label = { Text(text = "All") })
            }

            is SectorInput.CustomSector -> {
                // Text(text = input.sectorName)
                FilterChip(selected = selected, onClick = onOptionSelected, label = { Text(text = input.sectorName) })
            }
        }
    }
}

@Preview
@Composable
fun SectorPreview() {
    GPTInvestorTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var selectedAnswer by remember { mutableStateOf<SectorInput?>(null) }
            val possibleAnswers = listOf(
                SectorInput.AllSector,
                SectorInput.CustomSector("Technology"),
                SectorInput.CustomSector("Manufacturing"),
                SectorInput.CustomSector("Sports"),
                SectorInput.CustomSector("Security"),
                SectorInput.CustomSector("Fashion")
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
