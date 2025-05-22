package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NewAuthenticationScreen(modifier: Modifier, authViewModel: AuthenticationViewModel = hiltViewModel()) {
    val state = authViewModel.newAuthState.collectAsStateWithLifecycle()

    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state.value.authenticationScreen) {
            AuthenticationScreen.Login -> {
                LoginScreen(
                    modifier = Modifier,
                    email = state.value.email,
                    password = state.value.password,
                    enableButton = state.value.enableButton,
                    onLoginClick = {
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
                    enableButton = state.value.enableButton,
                    onLoginClick = {
                        authViewModel.handleEvent(AuthenticationEvent.GoToLoginScreen)
                    },
                    onSignUpClick = {
                    },
                    onEmailChange = {
                        authViewModel.handleEvent(AuthenticationEvent.EmailChanged(it))
                    },
                    onPasswordChange = {
                        authViewModel.handleEvent(AuthenticationEvent.PasswordChanged(it))
                    }
                )
            }
        }
    }
}
