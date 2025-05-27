package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun NewAuthenticationScreen(modifier: Modifier, authViewModel: AuthenticationViewModel = hiltViewModel(), onAuthenticationComplete: (String) -> Unit) {
    val state = authViewModel.newAuthState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authViewModel.actions.onEach { action ->
            when (action) {
                is AuthenticationAction.OnLogin -> {
                    onAuthenticationComplete(action.message)
                }
                is AuthenticationAction.OnSignUp -> {
                    onAuthenticationComplete(action.message)
                }
            }
        }.launchIn(scope)
    }

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
                    },
                    onLoginWithGoogleClick = {
                        authViewModel.handleEvent(AuthenticationEvent.LoginWithGoogle(context))
                    }
                )
            }

            AuthenticationScreen.SignUp -> {
                SignUpScreen(
                    modifier = Modifier,
                    email = state.value.email,
                    password = state.value.password,
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
                    onSignUpWithGoogleClick = {
                        authViewModel.handleEvent(AuthenticationEvent.SignUpWithGoogle(context))
                    }
                )
            }
        }
    }
}
