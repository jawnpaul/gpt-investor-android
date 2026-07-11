package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.layout.Box
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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ThemeDropdown(
    selectedOption: StringResource,
    options: List<StringResource>,
    onClick: (StringResource) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {}
) {
    var internalExpanded by remember { mutableStateOf(false) }
    val isExpanded = if (expanded) expanded else internalExpanded

    Box(
        modifier = modifier
            .padding(end = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                onExpandedChange(false)
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(option)) },
                    onClick = {
                        onExpandedChange(false)
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
