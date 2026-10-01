package com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.cancel
import com.thejawnpaul.gptinvestor.remove
import com.thejawnpaul.gptinvestor.remove_from_watchlist
import com.thejawnpaul.gptinvestor.remove_from_watchlist_confirmation
import org.jetbrains.compose.resources.stringResource

@Composable
fun RemoveFromWatchlistDialog(ticker: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(Res.string.remove_from_watchlist)) },
        text = { Text(text = stringResource(Res.string.remove_from_watchlist_confirmation, ticker)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(Res.string.remove), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.cancel))
            }
        }
    )
}
