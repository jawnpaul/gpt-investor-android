package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.guest_limit_reached_description
import com.thejawnpaul.gptinvestor.guest_limit_reached_title
import com.thejawnpaul.gptinvestor.sign_up
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestRateLimitBottomSheet(onDismiss: () -> Unit, onSignIn: () -> Unit, modifier: Modifier = Modifier) {
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.guest_limit_reached_title),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier,
        subtitle = stringResource(Res.string.guest_limit_reached_description)
    ) {
        GiButton(
            text = stringResource(Res.string.sign_up),
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
