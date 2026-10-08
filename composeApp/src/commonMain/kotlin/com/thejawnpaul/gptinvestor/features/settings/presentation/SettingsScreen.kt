package com.thejawnpaul.gptinvestor.features.settings.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.mohamedrejeb.calf.ui.dialog.AdaptiveAlertDialog
import com.mohamedrejeb.calf.ui.dialog.uikit.AlertDialogIosActionStyle
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.are_you_sure_you_want_to_delete_your_account
import com.thejawnpaul.gptinvestor.cancel
import com.thejawnpaul.gptinvestor.core.navigation.BackButton
import com.thejawnpaul.gptinvestor.delete
import com.thejawnpaul.gptinvestor.delete_account
import com.thejawnpaul.gptinvestor.features.settings.presentation.state.SettingsView
import com.thejawnpaul.gptinvestor.settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    state: SettingsView,
    onEvent: (SettingsEvent) -> Unit,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding), contentAlignment = Alignment.TopStart) {
            Column(modifier = Modifier) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(onClick = { onAction(SettingsAction.OnGoBack) })

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
                    AdaptiveAlertDialog(
                        onConfirm = {
                            onEvent(SettingsEvent.DeleteAccount)
                            showDeleteDialog = false
                        },
                        onDismiss = { showDeleteDialog = false },
                        confirmText = stringResource(Res.string.delete),
                        dismissText = stringResource(Res.string.cancel),
                        title = stringResource(Res.string.delete_account),
                        text = stringResource(Res.string.are_you_sure_you_want_to_delete_your_account),
                        materialConfirmButton = {
                            TextButton(
                                onClick = {
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
                        materialDismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text(stringResource(Res.string.cancel))
                            }
                        },
                        iosConfirmButtonStyle = AlertDialogIosActionStyle.Destructive,
                        iosDismissButtonStyle = AlertDialogIosActionStyle.Cancel
                    )
                }
            }
        }
    }
}
