package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewAuthenticationScreen(modifier: Modifier, authViewModel: AuthenticationViewModel = koinViewModel(), onAuthenticationComplete: (String) -> Unit) {
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
                        authViewModel.handleEvent(AuthenticationEvent.SignUpWithGoogle(context = context))
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
