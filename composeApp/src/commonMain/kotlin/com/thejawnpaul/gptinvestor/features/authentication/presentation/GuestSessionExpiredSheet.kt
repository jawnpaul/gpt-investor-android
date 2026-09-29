package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.core.session.GuestSessionReason
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.guest_session_expired_body_expired
import com.thejawnpaul.gptinvestor.guest_session_expired_body_limit
import com.thejawnpaul.gptinvestor.guest_session_expired_have_account
import com.thejawnpaul.gptinvestor.guest_session_expired_sign_up
import com.thejawnpaul.gptinvestor.guest_session_expired_title_expired
import com.thejawnpaul.gptinvestor.guest_session_expired_title_limit
import org.jetbrains.compose.resources.stringResource

@Composable
fun GuestSessionExpiredSheet(
    reason: GuestSessionReason,
    onDismiss: () -> Unit,
    onSignUp: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    GptInvestorBottomSheet(onDismiss = onDismiss, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    if (reason == GuestSessionReason.Expired) {
                        Res.string.guest_session_expired_title_expired
                    } else {
                        Res.string.guest_session_expired_title_limit
                    }
                ),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(
                    if (reason == GuestSessionReason.Expired) {
                        Res.string.guest_session_expired_body_expired
                    } else {
                        Res.string.guest_session_expired_body_limit
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = onSignUp
            ) {
                Text(text = stringResource(Res.string.guest_session_expired_sign_up))
            }
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = onLogin
            ) {
                Text(text = stringResource(Res.string.guest_session_expired_have_account))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
