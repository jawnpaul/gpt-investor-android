package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.apple_icon
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.baseline_visibility_off_24
import com.thejawnpaul.gptinvestor.core.platform.Platform
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.platform.PlatformType
import com.thejawnpaul.gptinvestor.don_t_have_an_account
import com.thejawnpaul.gptinvestor.email_address
import com.thejawnpaul.gptinvestor.google_icon
import com.thejawnpaul.gptinvestor.ic_lock
import com.thejawnpaul.gptinvestor.ic_profile
import com.thejawnpaul.gptinvestor.log_in_with_apple
import com.thejawnpaul.gptinvestor.log_in_with_google
import com.thejawnpaul.gptinvestor.login
import com.thejawnpaul.gptinvestor.or
import com.thejawnpaul.gptinvestor.outline_visibility_24
import com.thejawnpaul.gptinvestor.password
import com.thejawnpaul.gptinvestor.sign_up
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.linkMedium
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(state: LoginUiState, onEvent: (LoginUiEvent) -> Unit, modifier: Modifier = Modifier) {
    var passwordHidden by remember { mutableStateOf(true) }
    val platformContext: PlatformContext = koinInject()

    Scaffold(modifier = modifier, topBar = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onEvent(LoginUiEvent.GoBack) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.back)
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.login),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    value = state.email,
                    onValueChange = { onEvent(LoginUiEvent.EmailChanged(it)) },
                    label = {
                        Text(text = stringResource(Res.string.email_address))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_profile),
                            contentDescription = null
                        )
                    }
                )
                // Input
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    value = state.password,
                    onValueChange = { onEvent(LoginUiEvent.PasswordChanged(it)) },
                    label = {
                        Text(text = stringResource(Res.string.password))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordHidden) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordHidden = !passwordHidden
                        }) {
                            val visibilityIcon =
                                if (passwordHidden) {
                                    Res.drawable.outline_visibility_24
                                } else {
                                    Res.drawable.baseline_visibility_off_24
                                }

                            Icon(
                                painter = painterResource(visibilityIcon),
                                contentDescription = null
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_lock),
                            contentDescription = null
                        )
                    }
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    onClick = { onEvent(LoginUiEvent.LoginClick) },
                    enabled = state.enableButton
                ) {
                    Text(text = stringResource(Res.string.login).uppercase())
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.or).uppercase(),
                    textAlign = TextAlign.Center
                )

                if (Platform.type == PlatformType.IOS) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEvent(LoginUiEvent.LoginWithApple) },
                        enabled = !state.loading
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(Res.drawable.apple_icon),
                                contentDescription = null
                            )
                            Text(text = stringResource(Res.string.log_in_with_apple))
                        }
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(LoginUiEvent.LoginWithGoogle(platformContext = platformContext)) },
                    enabled = !state.loading
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(Res.drawable.google_icon),
                            contentDescription = null
                        )
                        Text(text = stringResource(Res.string.log_in_with_google))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 8.dp),
                        text = stringResource(Res.string.don_t_have_an_account),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = { onEvent(LoginUiEvent.GoToSignUp) }
                        ),
                        text = stringResource(Res.string.sign_up),
                        textDecoration = TextDecoration.Underline,
                        style = MaterialTheme.typography.linkMedium
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginPreview() {
    GPTInvestorTheme {
        LoginScreen(
            modifier = Modifier,
            state = LoginUiState(),
            onEvent = {}
        )
    }
}
