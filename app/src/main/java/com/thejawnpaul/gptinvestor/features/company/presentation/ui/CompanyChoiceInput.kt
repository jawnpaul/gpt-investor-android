package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanyChoiceQuestion(possibleAnswers: List<String>, selectedAnswer: String?, onOptionSelected: (String) -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier.padding(start = 8.dp, end = 8.dp)) {
        possibleAnswers.forEach {
            val selected = it == selectedAnswer
            SingleCompanyChoice(
                modifier = Modifier.padding(horizontal = 4.dp),
                company = it,
                selected = selected,
                enabled = enabled,
                onOptionSelected = { onOptionSelected(it) }
            )
        }
    }
}

@Composable
fun SingleCompanyChoice(company: String, selected: Boolean, enabled: Boolean, onOptionSelected: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected,
                enabled = enabled,
                onClick = onOptionSelected,
                role = Role.RadioButton
            )
    ) {
        FilterChip(
            selected = selected,
            enabled = enabled,
            onClick = onOptionSelected,
            label = { Text(text = company) }
        )
    }
}
