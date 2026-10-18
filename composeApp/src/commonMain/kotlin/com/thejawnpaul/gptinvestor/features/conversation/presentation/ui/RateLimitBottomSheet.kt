package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.rate_limit_bottom_sheet_description
import com.thejawnpaul.gptinvestor.rate_limit_bottom_sheet_title
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.upgrade
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateLimitBottomSheet(onDismiss: () -> Unit, onUpgrade: () -> Unit, modifier: Modifier = Modifier) {
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.rate_limit_bottom_sheet_title),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier,
        subtitle = stringResource(Res.string.rate_limit_bottom_sheet_description)
    ) {
        GiButton(
            text = stringResource(Res.string.upgrade),
            onClick = onUpgrade,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
