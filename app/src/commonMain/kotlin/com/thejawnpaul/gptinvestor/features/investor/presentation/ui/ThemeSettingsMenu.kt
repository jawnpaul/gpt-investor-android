package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.ic_arrow_down
import gptinvestor.app.generated.resources.ic_sun
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ThemeDropdown(modifier: Modifier, onClick: (String) -> Unit, options: List<String>, selectedOption: String) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .padding(end = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(end = 0.dp)
                .clickable(indication = null, interactionSource = null, onClick = {
                    expanded = !expanded
                }),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_sun),
                contentDescription = null
            )
            Icon(
                imageVector = vectorResource(Res.drawable.ic_arrow_down),
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        expanded = false
                        onClick(option)
                    },
                    leadingIcon = {
                        if (option == selectedOption) {
                            Icon(Icons.Default.Done, contentDescription = "More options")
                        }
                    }
                )
            }
        }
    }
}
