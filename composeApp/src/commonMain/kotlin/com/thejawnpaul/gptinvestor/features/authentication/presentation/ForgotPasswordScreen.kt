package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.email_address
import com.thejawnpaul.gptinvestor.email_placeholder
import com.thejawnpaul.gptinvestor.error_email_invalid
import com.thejawnpaul.gptinvestor.forgot_password_back_to_login
import com.thejawnpaul.gptinvestor.forgot_password_body
import com.thejawnpaul.gptinvestor.forgot_password_error_rate_limited
import com.thejawnpaul.gptinvestor.forgot_password_resend
import com.thejawnpaul.gptinvestor.forgot_password_resend_in
import com.thejawnpaul.gptinvestor.forgot_password_send
import com.thejawnpaul.gptinvestor.forgot_password_sent_body
import com.thejawnpaul.gptinvestor.forgot_password_sent_title
import com.thejawnpaul.gptinvestor.forgot_password_title
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiFillScrollColumn
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextField
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiTopBarTitleStyle
import org.jetbrains.compose.resources.stringResource

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordUiState,
    onEvent: (ForgotPasswordUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val scrolled by remember { derivedStateOf { scrollState.value > 0 } }
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GiTopBar(
                title = stringResource(Res.string.forgot_password_title),
                onBack = { onEvent(ForgotPasswordUiEvent.GoBack) },
                backContentDescription = stringResource(Res.string.back),
                scrolled = scrolled,
                titleStyle = GiTopBarTitleStyle.Page
            )
        }
    ) { innerPadding ->
        GiFillScrollColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x4, bottom = GiSpace.x4),
            scrollState = scrollState
        ) {
            AnimatedContent(
                targetState = state.sentTo,
                transitionSpec = {
                    fadeIn(tween(GiMotion.BASE, delayMillis = GiMotion.INSTANT, easing = EaseOutQuart)) togetherWith
                        fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
                }
            ) { sentTo ->
                if (sentTo == null) {
                    ForgotPasswordForm(state = state, onEvent = onEvent)
                } else {
                    ForgotPasswordSent(state = state, sentTo = sentTo, onEvent = onEvent)
                }
            }
            AnimatedVisibility(
                visible = state.rateLimited,
                enter = expandVertically(GiMotion.smooth()) +
                    fadeIn(tween(GiMotion.BASE, delayMillis = GiMotion.INSTANT, easing = EaseOutQuart)),
                exit = shrinkVertically(GiMotion.smooth()) + fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
            ) {
                GiBanner(
                    title = stringResource(Res.string.forgot_password_error_rate_limited),
                    modifier = Modifier
                        .padding(top = GiSpace.x4)
                        .semantics { liveRegion = LiveRegionMode.Assertive },
                    icon = GiIcons.warningCircle,
                    tone = GiBannerTone.Error
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordForm(
    state: ForgotPasswordUiState,
    onEvent: (ForgotPasswordUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
        GiText(
            text = stringResource(Res.string.forgot_password_body),
            style = GiTextStyle.Body,
            color = GiTheme.colors.muted
        )
        GiTextField(
            value = state.email,
            onValueChange = { onEvent(ForgotPasswordUiEvent.EmailChanged(it)) },
            label = stringResource(Res.string.email_address),
            placeholder = stringResource(Res.string.email_placeholder),
            errorText = if (state.emailError) stringResource(Res.string.error_email_invalid) else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onEvent(ForgotPasswordUiEvent.SendClick) }),
            leadingIcon = GiIcons.envelopeSimple
        )
        GiButton(
            text = stringResource(Res.string.forgot_password_send),
            onClick = { onEvent(ForgotPasswordUiEvent.SendClick) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = GiSpace.x2),
            enabled = state.isSendEnabled,
            loading = state.sending
        )
    }
}

@Composable
private fun ForgotPasswordSent(
    state: ForgotPasswordUiState,
    sentTo: String,
    onEvent: (ForgotPasswordUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        GiBanner(
            title = stringResource(Res.string.forgot_password_sent_title),
            modifier = Modifier
                .padding(bottom = GiSpace.x3)
                .semantics { liveRegion = LiveRegionMode.Polite },
            subtitle = stringResource(Res.string.forgot_password_sent_body, sentTo),
            icon = GiIcons.envelopeSimple,
            tone = GiBannerTone.Violet
        )
        GiButton(
            text = stringResource(Res.string.forgot_password_back_to_login),
            onClick = { onEvent(ForgotPasswordUiEvent.GoBack) },
            modifier = Modifier.fillMaxWidth()
        )
        GiButton(
            text = if (state.resendSecondsLeft > 0) {
                stringResource(Res.string.forgot_password_resend_in, state.resendSecondsLeft)
            } else {
                stringResource(Res.string.forgot_password_resend)
            },
            onClick = { onEvent(ForgotPasswordUiEvent.ResendClick) },
            modifier = Modifier.fillMaxWidth(),
            variant = GiButtonVariant.Secondary,
            enabled = state.isResendEnabled,
            loading = state.sending
        )
    }
}

@Composable
private fun ForgotPasswordScreenPreview(state: ForgotPasswordUiState) {
    GPTInvestorTheme {
        ForgotPasswordScreen(state = state, onEvent = {})
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun ForgotPasswordFormPreview() {
    ForgotPasswordScreenPreview(state = ForgotPasswordUiState(email = "diogo@example.com"))
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun ForgotPasswordSentPreview() {
    ForgotPasswordScreenPreview(
        state = ForgotPasswordUiState(
            email = "diogo@example.com",
            sentTo = "diogo@example.com",
            resendSecondsLeft = 42
        )
    )
}

@PreviewLightDark
@Composable
private fun ForgotPasswordRateLimitedPreview() {
    ForgotPasswordScreenPreview(
        state = ForgotPasswordUiState(email = "diogo@example.com", rateLimited = true)
    )
}
