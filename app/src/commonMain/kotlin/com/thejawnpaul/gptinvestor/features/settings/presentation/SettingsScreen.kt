package com.thejawnpaul.gptinvestor.features.settings.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.settings.presentation.state.SettingsView
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.are_you_sure_you_want_to_delete_your_account
import gptinvestor.app.generated.resources.back
import gptinvestor.app.generated.resources.cancel
import gptinvestor.app.generated.resources.delete
import gptinvestor.app.generated.resources.delete_account
import gptinvestor.app.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(modifier: Modifier, state: SettingsView, onEvent: (SettingsEvent) -> Unit, onAction: (SettingsAction) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Column(modifier = Modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

            // Delete Account Button
            ElevatedButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.delete_account),
                    color = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Delete Account Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(text = stringResource(Res.string.delete_account))
                    },
                    text = {
                        Text(
                            text = stringResource(Res.string.are_you_sure_you_want_to_delete_your_account)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // Handle account deletion here
                                onEvent(SettingsEvent.DeleteAccount)
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(Res.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text(stringResource(Res.string.cancel))
                        }
                    }
                )
            }
        }
    }
}
