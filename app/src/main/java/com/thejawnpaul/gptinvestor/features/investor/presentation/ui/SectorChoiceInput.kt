package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun SectorChoiceQuestion(possibleAnswers: List<SectorInput>, selectedAnswer: SectorInput?, onOptionSelected: (SectorInput) -> Unit, modifier: Modifier = Modifier) {
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
fun SingleSectorChoice(input: SectorInput, selected: Boolean, onOptionSelected: () -> Unit, modifier: Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    val text = when (input) {
        is SectorInput.AllSector -> {
            " All "
        }

        else -> {
            (input as SectorInput.CustomSector).sectorName
        }
    }

    if (selected) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
            color = gptInvestorColors.utilColors.borderBright10,
            onClick = onOptionSelected
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
            border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.outlineVariant),
            onClick = onOptionSelected
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = gptInvestorColors.textColors.secondary50
            )
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
