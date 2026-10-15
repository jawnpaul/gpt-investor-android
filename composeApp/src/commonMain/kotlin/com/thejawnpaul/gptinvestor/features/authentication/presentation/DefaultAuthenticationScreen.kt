package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.auth_landing_tagline
import com.thejawnpaul.gptinvestor.auth_legal
import com.thejawnpaul.gptinvestor.continue_as_guest
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.privacy_policy
import com.thejawnpaul.gptinvestor.sign_up
import com.thejawnpaul.gptinvestor.terms
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBrandMark
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiFillScrollColumn
import com.thejawnpaul.gptinvestor.theme.components.GiLink
import com.thejawnpaul.gptinvestor.theme.components.GiLinkText
import com.thejawnpaul.gptinvestor.theme.components.GiLinkTextTone
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiWaveIllustration
import org.jetbrains.compose.resources.stringResource

@Composable
fun DefaultAuthenticationScreen(
    onEvent: (DefaultAuthenticationEvent) -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false
) {
    val colors = GiTheme.colors
    val terms = stringResource(Res.string.terms)
    val privacyPolicy = stringResource(Res.string.privacy_policy)
    Scaffold(
        modifier = modifier,
        containerColor = colors.bg,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        GiFillScrollColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = GiSpace.x3, bottom = GiSpace.x4)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x10 + GiSpace.x5),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
            ) {
                GiBrandMark()
                GiText(
                    text = stringResource(Res.string.gpt_investor),
                    modifier = Modifier.semantics { heading() },
                    style = GiTextStyle.Display,
                    color = colors.ink,
                    textAlign = TextAlign.Center
                )
                GiText(
                    text = stringResource(Res.string.auth_landing_tagline),
                    style = GiTextStyle.Input,
                    color = colors.muted,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            GiWaveIllustration()
            Column(
                modifier = Modifier.padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x4),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
            ) {
                GiButton(
                    text = stringResource(Res.string.sign_up),
                    onClick = { onEvent(DefaultAuthenticationEvent.SignUp) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                )
                GiButton(
                    text = stringResource(Res.string.continue_as_guest),
                    onClick = { onEvent(DefaultAuthenticationEvent.GuestLogin) },
                    modifier = Modifier.fillMaxWidth(),
                    size = GiButtonSize.Medium,
                    variant = GiButtonVariant.Text,
                    loading = loading
                )
                GiLinkText(
                    text = stringResource(Res.string.auth_legal, terms, privacyPolicy),
                    links = listOf(
                        GiLink(terms) { onEvent(DefaultAuthenticationEvent.OpenTerms) },
                        GiLink(privacyPolicy) { onEvent(DefaultAuthenticationEvent.OpenPrivacyPolicy) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = GiSpace.x3),
                    tone = GiLinkTextTone.Legal
                )
            }
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun DefaultAuthenticationScreenPreview() {
    GPTInvestorTheme {
        DefaultAuthenticationScreen(onEvent = {})
    }
}

@PreviewLightDark
@Composable
private fun DefaultAuthenticationScreenLoadingPreview() {
    GPTInvestorTheme {
        DefaultAuthenticationScreen(onEvent = {}, loading = true)
    }
}
