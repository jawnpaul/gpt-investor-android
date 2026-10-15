package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.apple_icon
import com.thejawnpaul.gptinvestor.auth_have_account
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.core.platform.Platform
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.platform.PlatformType
import com.thejawnpaul.gptinvestor.email_address
import com.thejawnpaul.gptinvestor.email_conflict_body
import com.thejawnpaul.gptinvestor.email_conflict_title
import com.thejawnpaul.gptinvestor.email_placeholder
import com.thejawnpaul.gptinvestor.error_email_invalid
import com.thejawnpaul.gptinvestor.google_icon
import com.thejawnpaul.gptinvestor.hide_password
import com.thejawnpaul.gptinvestor.login
import com.thejawnpaul.gptinvestor.name
import com.thejawnpaul.gptinvestor.or
import com.thejawnpaul.gptinvestor.password
import com.thejawnpaul.gptinvestor.show_password
import com.thejawnpaul.gptinvestor.sign_up
import com.thejawnpaul.gptinvestor.sign_up_with_apple
import com.thejawnpaul.gptinvestor.sign_up_with_google
import com.thejawnpaul.gptinvestor.signup_error_name
import com.thejawnpaul.gptinvestor.signup_error_password_short
import com.thejawnpaul.gptinvestor.signup_name_placeholder
import com.thejawnpaul.gptinvestor.signup_password_placeholder
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiFillScrollColumn
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiLink
import com.thejawnpaul.gptinvestor.theme.components.GiLinkText
import com.thejawnpaul.gptinvestor.theme.components.GiOrDivider
import com.thejawnpaul.gptinvestor.theme.components.GiTextField
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiTopBarTitleStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SignUpScreen(
    state: SignUpUiState,
    onEvent: (SignUpUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    invalidSubmits: Flow<SignUpField> = emptyFlow(),
    platformContext: PlatformContext = koinInject()
) {
    val scrollState = rememberScrollState()
    val scrolled by remember { derivedStateOf { scrollState.value > 0 } }
    val haptics = LocalHapticFeedback.current
    val focusRequesters = remember { SignUpField.entries.associateWith { FocusRequester() } }
    LaunchedEffect(invalidSubmits) {
        invalidSubmits.collect { field ->
            haptics.performHapticFeedback(GiHaptics.Reject)
            focusRequesters.getValue(field).requestFocus()
        }
    }
    val loadingMethod = state.loadingMethod
    val loginText = stringResource(Res.string.login)
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GiTopBar(
                title = stringResource(Res.string.sign_up),
                onBack = { onEvent(SignUpUiEvent.GoBack) },
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
                .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x3, bottom = GiSpace.x4),
            scrollState = scrollState
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
                GiTextField(
                    value = state.name,
                    onValueChange = { onEvent(SignUpUiEvent.NameChanged(it)) },
                    label = stringResource(Res.string.name),
                    modifier = Modifier.focusRequester(focusRequesters.getValue(SignUpField.Name)),
                    placeholder = stringResource(Res.string.signup_name_placeholder),
                    errorText = if (state.nameError) stringResource(Res.string.signup_error_name) else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = GiIcons.user
                )
                GiTextField(
                    value = state.email,
                    onValueChange = { onEvent(SignUpUiEvent.EmailChanged(it)) },
                    label = stringResource(Res.string.email_address),
                    modifier = Modifier.focusRequester(focusRequesters.getValue(SignUpField.Email)),
                    placeholder = stringResource(Res.string.email_placeholder),
                    errorText = if (state.emailError) stringResource(Res.string.error_email_invalid) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    leadingIcon = GiIcons.envelopeSimple
                )
                GiTextField(
                    value = state.password,
                    onValueChange = { onEvent(SignUpUiEvent.PasswordChanged(it)) },
                    label = stringResource(Res.string.password),
                    modifier = Modifier.focusRequester(focusRequesters.getValue(SignUpField.Password)),
                    placeholder = stringResource(Res.string.signup_password_placeholder),
                    errorText = if (state.passwordError) {
                        stringResource(
                            Res.string.signup_error_password_short
                        )
                    } else {
                        null
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onEvent(SignUpUiEvent.SignUpClick) }),
                    leadingIcon = GiIcons.lock,
                    isPassword = true,
                    showPasswordContentDescription = stringResource(Res.string.show_password),
                    hidePasswordContentDescription = stringResource(Res.string.hide_password)
                )
                AnimatedVisibility(
                    visible = state.showEmailConflictPrompt,
                    enter = expandVertically(GiMotion.smooth()) +
                        fadeIn(tween(GiMotion.BASE, delayMillis = GiMotion.INSTANT, easing = EaseOutQuart)),
                    exit = shrinkVertically(GiMotion.smooth()) + fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
                ) {
                    GiBanner(
                        title = stringResource(Res.string.email_conflict_title),
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
                        subtitle = stringResource(Res.string.email_conflict_body),
                        icon = GiIcons.warningCircle,
                        tone = GiBannerTone.Error
                    ) {
                        GiButton(
                            text = loginText,
                            onClick = { onEvent(SignUpUiEvent.GoToLogin) },
                            size = GiButtonSize.Small
                        )
                    }
                }
                GiButton(
                    text = stringResource(Res.string.sign_up),
                    onClick = { onEvent(SignUpUiEvent.SignUpClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = GiSpace.x2),
                    enabled = state.isSignUpEnabled,
                    loading = loadingMethod == AuthMethod.Email
                )
            }
            GiOrDivider(text = stringResource(Res.string.or), modifier = Modifier.padding(top = GiSpace.x6))
            Column(
                modifier = Modifier.padding(top = GiSpace.x4),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
            ) {
                if (Platform.type == PlatformType.IOS) {
                    GiButton(
                        text = stringResource(Res.string.sign_up_with_apple),
                        onClick = { onEvent(SignUpUiEvent.SingUpWithApple) },
                        modifier = Modifier.fillMaxWidth(),
                        variant = GiButtonVariant.Secondary,
                        leadingPainter = painterResource(Res.drawable.apple_icon),
                        enabled = state.isAppleEnabled,
                        loading = loadingMethod == AuthMethod.Apple
                    )
                }
                GiButton(
                    text = stringResource(Res.string.sign_up_with_google),
                    onClick = { onEvent(SignUpUiEvent.SignUpWithGoogle(platformContext = platformContext)) },
                    modifier = Modifier.fillMaxWidth(),
                    variant = GiButtonVariant.Secondary,
                    leadingPainter = painterResource(Res.drawable.google_icon),
                    tintLeadingIcon = false,
                    enabled = state.isGoogleEnabled,
                    loading = loadingMethod == AuthMethod.Google
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            GiLinkText(
                text = stringResource(Res.string.auth_have_account, loginText),
                links = listOf(GiLink(loginText) { onEvent(SignUpUiEvent.GoToLogin) }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = GiSpace.x8)
            )
        }
    }
}

@Composable
private fun SignUpScreenPreview(state: SignUpUiState) {
    GPTInvestorTheme {
        SignUpScreen(
            state = state,
            onEvent = {},
            platformContext = object : PlatformContext {}
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SignUpEmptyPreview() {
    SignUpScreenPreview(state = SignUpUiState())
}

@PreviewLightDark
@Composable
private fun SignUpInvalidPreview() {
    SignUpScreenPreview(
        state = SignUpUiState(
            name = "Test",
            email = "notanemail",
            password = "abc",
            emailError = true,
            passwordError = true
        )
    )
}

@PreviewLightDark
@Composable
private fun SignUpExistsPreview() {
    SignUpScreenPreview(
        state = SignUpUiState(
            name = "Test",
            email = "diogo@example.com",
            password = "investor2026",
            showEmailConflictPrompt = true
        )
    )
}

@PreviewLightDark
@Composable
private fun SignUpLoadingPreview() {
    SignUpScreenPreview(
        state = SignUpUiState(
            name = "Test",
            email = "diogo@example.com",
            password = "investor2026",
            loadingMethod = AuthMethod.Email
        )
    )
}
