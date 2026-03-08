package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewAuthenticationScreen(modifier: Modifier = Modifier, authViewModel: AuthenticationViewModel = koinViewModel()) {
    val state = authViewModel.newAuthState.collectAsStateWithLifecycle()

    Box(
        modifier.background(color = Color(0xFF0C0810)),
        contentAlignment = Alignment.Center
    ) {
        when (state.value.authenticationScreen) {
            AuthenticationScreen.Login -> {
                LoginScreen(
                    modifier = Modifier,
                    email = state.value.email,
                    password = state.value.password,
                    enableButton = state.value.enableButton,
                    loading = state.value.loading,
                    onLoginClick = {
                        authViewModel.handleEvent(AuthenticationEvent.Login)
                    },
                    onSignUpClick = {
                        authViewModel.handleEvent(AuthenticationEvent.GoToSignUpScreen)
                    },
                    onEmailChange = {
                        authViewModel.handleEvent(AuthenticationEvent.EmailChanged(it))
                    },
                    onPasswordChange = {
                        authViewModel.handleEvent(AuthenticationEvent.PasswordChanged(it))
                    }
                )
            }

            AuthenticationScreen.SignUp -> {
                SignUpScreen(
                    modifier = Modifier,
                    email = state.value.email,
                    password = state.value.password,
                    name = state.value.name,
                    loading = state.value.loading,
                    enableButton = state.value.enableButton,
                    onLoginClick = {
                        authViewModel.handleEvent(AuthenticationEvent.GoToLoginScreen)
                    },
                    onSignUpClick = {
                        authViewModel.handleEvent(AuthenticationEvent.SignUp)
                    },
                    onEmailChange = {
                        authViewModel.handleEvent(AuthenticationEvent.EmailChanged(it))
                    },
                    onPasswordChange = {
                        authViewModel.handleEvent(AuthenticationEvent.PasswordChanged(it))
                    },
                    onNameChange = {
                        authViewModel.handleEvent(AuthenticationEvent.NameChanged(it))
                    }
                )
            }
        }
    }
}
