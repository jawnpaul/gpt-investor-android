package com.thejawnpaul.gptinvestor.features.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.are_you_sure_you_want_to_delete_your_account
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.cancel
import com.thejawnpaul.gptinvestor.delete
import com.thejawnpaul.gptinvestor.delete_account
import com.thejawnpaul.gptinvestor.digest_delivery_time
import com.thejawnpaul.gptinvestor.done
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.settings.presentation.state.SettingsView
import com.thejawnpaul.gptinvestor.settings
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    state: SettingsView,
    onEvent: (SettingsEvent) -> Unit,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding), contentAlignment = Alignment.TopStart) {
            Column(modifier = Modifier) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onAction(SettingsAction.OnGoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                    Text(
                        text = stringResource(Res.string.settings),
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DigestTimeRow(
                        hourLabel = state.digestDeliveryHourLabel,
                        onClick = { onEvent(SettingsEvent.ShowTimePicker) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ElevatedButton(
                    onClick = { onEvent(SettingsEvent.ShowDeleteDialog) },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.delete_account),
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        if (state.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { onEvent(SettingsEvent.DismissDeleteDialog) },
                title = { Text(text = stringResource(Res.string.delete_account)) },
                text = {
                    Text(
                        text = stringResource(Res.string.are_you_sure_you_want_to_delete_your_account)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { onEvent(SettingsEvent.DeleteAccount) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(Res.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(SettingsEvent.DismissDeleteDialog) }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            )
        }

        if (state.showTimePicker) {
            DigestTimePickerSheet(
                currentHour = state.digestDeliveryHour,
                onSelect = { onEvent(SettingsEvent.UpdateDigestTime(it)) },
                onDismiss = { onEvent(SettingsEvent.DismissTimePicker) }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
    GPTInvestorTheme {
        SettingsScreen(
            state = SettingsView(
                digestDeliveryHour = 7,
                digestDeliveryHourLabel = "7:00 AM"
            ),
            onEvent = {},
            onAction = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsTimePickerPreview() {
    GPTInvestorTheme {
        SettingsScreen(
            state = SettingsView(
                digestDeliveryHour = 7,
                digestDeliveryHourLabel = "7:00 AM",
                showTimePicker = true
            ),
            onEvent = {},
            onAction = {}
        )
    }
}

@Composable
private fun DigestTimeRow(hourLabel: String?, onClick: () -> Unit) {
    val customColors = LocalGPTInvestorColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.digest_delivery_time),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (hourLabel != null) {
                Text(
                    text = hourLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textColors.secondary50
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = customColors.textColors.secondary50
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DigestTimePickerSheet(currentHour: Int?, onSelect: (Int) -> Unit, onDismiss: () -> Unit) {
    GptInvestorBottomSheet(onDismiss = onDismiss) {
        Text(
            text = stringResource(Res.string.digest_delivery_time),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        key(currentHour) {
            val timePickerState = rememberTimePickerState(
                initialHour = currentHour ?: 7,
                initialMinute = 0,
                is24Hour = false
            )

            TimePicker(
                state = timePickerState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { onSelect(timePickerState.hour) }) {
                    Text(stringResource(Res.string.done))
                }
            }
        }
    }
}
