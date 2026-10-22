package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.core.session.GuestSessionReason
import com.thejawnpaul.gptinvestor.guest_session_expired_body_expired
import com.thejawnpaul.gptinvestor.guest_session_expired_body_limit
import com.thejawnpaul.gptinvestor.guest_session_expired_body_questions
import com.thejawnpaul.gptinvestor.guest_session_expired_have_account
import com.thejawnpaul.gptinvestor.guest_session_expired_sign_up
import com.thejawnpaul.gptinvestor.guest_session_expired_title_expired
import com.thejawnpaul.gptinvestor.guest_session_expired_title_limit
import com.thejawnpaul.gptinvestor.guest_session_expired_title_questions
import com.thejawnpaul.gptinvestor.login
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import org.jetbrains.compose.resources.stringResource

// v4 sheet pattern: StatNumber title and body in the header, then primary over secondary CTA (both L).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestSessionExpiredSheet(
    reason: GuestSessionReason,
    onDismiss: () -> Unit,
    onSignUp: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(
            when (reason) {
                GuestSessionReason.Expired -> Res.string.guest_session_expired_title_expired
                GuestSessionReason.Limit -> Res.string.guest_session_expired_title_limit
                GuestSessionReason.QuestionLimit -> Res.string.guest_session_expired_title_questions
            }
        ),
        subtitle = stringResource(
            when (reason) {
                GuestSessionReason.Expired -> Res.string.guest_session_expired_body_expired
                GuestSessionReason.Limit -> Res.string.guest_session_expired_body_limit
                GuestSessionReason.QuestionLimit -> Res.string.guest_session_expired_body_questions
            }
        ),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiButton(
                text = stringResource(Res.string.guest_session_expired_sign_up),
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth()
            )
            GiButton(
                text = stringResource(
                    if (reason == GuestSessionReason.QuestionLimit) {
                        Res.string.login
                    } else {
                        Res.string.guest_session_expired_have_account
                    }
                ),
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                variant = GiButtonVariant.Secondary
            )
        }
    }
}
