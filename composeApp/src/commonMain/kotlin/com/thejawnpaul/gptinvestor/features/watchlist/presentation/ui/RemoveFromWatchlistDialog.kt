package com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalHapticFeedback
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.cancel
import com.thejawnpaul.gptinvestor.remove
import com.thejawnpaul.gptinvestor.theme.components.GiDialog
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.watchlist_remove_body
import com.thejawnpaul.gptinvestor.watchlist_remove_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RemoveFromWatchlistDialog(
    ticker: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    companyName: String? = null
) {
    val haptics = LocalHapticFeedback.current
    GiDialog(
        title = stringResource(Res.string.watchlist_remove_title, ticker),
        body = stringResource(Res.string.watchlist_remove_body, companyName?.takeIf { it.isNotBlank() } ?: ticker),
        confirmText = stringResource(Res.string.remove),
        onConfirm = {
            haptics.performHapticFeedback(GiHaptics.Reject)
            onConfirm()
        },
        dismissText = stringResource(Res.string.cancel),
        onDismiss = onDismiss,
        icon = GiIcons.trash,
        destructive = true
    )
}
