package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.baseline_visibility_off_24
import gptinvestor.app.generated.resources.done
import gptinvestor.app.generated.resources.email_address
import gptinvestor.app.generated.resources.login
import gptinvestor.app.generated.resources.outline_visibility_24
import gptinvestor.app.generated.resources.password
import gptinvestor.app.generated.resources.retry_sign_in
import gptinvestor.app.generated.resources.sign_in
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthenticationScreen(viewModel: AuthenticationViewModel = koinViewModel(), onSignInSuccess: () -> Unit) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isUserSignedIn = authState.isUserSignedIn
    var passwordHidden by remember { mutableStateOf(true) }

    LaunchedEffect(isUserSignedIn) {
        if (isUserSignedIn) {
            onSignInSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (authState.loading) {
            CircularProgressIndicator()
        } else if (authState.errorMessage != null) {
            Text(
                text = "Error: ${authState.errorMessage}",
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.signIn()
                }
            ) {
                Text(stringResource(Res.string.retry_sign_in))
            }
        } else {
            if (authState.showLoginInput) {
                AnimatedVisibility(visible = true) {
                    Column {
                        // Input
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            value = authState.email,
                            onValueChange = {
                                viewModel.updateEmail(it)
                            },
                            label = {
                                Text(text = stringResource(Res.string.email_address))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Input
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            value = authState.password,
                            onValueChange = {
                                viewModel.updatePassword(it)
                            },
                            label = {
                                Text(text = stringResource(Res.string.password))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordHidden = !passwordHidden
                                }) {
                                    val visibilityIcon =
                                        if (passwordHidden) Res.drawable.outline_visibility_24 else Res.drawable.baseline_visibility_off_24

                                    Icon(
                                        painter = painterResource(visibilityIcon),
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                        // Done button
                        Button(
                            enabled = authState.enableLoginButton,
                            onClick = {
                                viewModel.login()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(Res.string.done))
                        }
                    }
                }
            } else {
                Button(
                    onClick = {
                        viewModel.signIn()
                    }
                ) {
                    Text(stringResource(Res.string.sign_in))
                }

                Button(onClick = {
                    viewModel.showLoginInput()
                }) {
                    Text(stringResource(Res.string.login))
                }
            }
        }
    }
}
