package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationViewModel
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginUiAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginViewModel
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpUiAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.authenticationNavGraph(
    navController: NavHostController,
    platformActions: PlatformActions,
    hasCompletedPostAuthOnboarding: Boolean
) {
    composable(route = Screen.DefaultAuthenticationScreen.route) {
        val viewModel = koinViewModel<DefaultAuthenticationViewModel>()
        val state = viewModel.loading.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    DefaultAuthenticationAction.OnGoToHome -> {
                        navigateAfterAuth(
                            navController,
                            Screen.DefaultAuthenticationScreen.route,
                            hasCompletedPostAuthOnboarding
                        )
                    }

                    DefaultAuthenticationAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route)
                    }

                    is DefaultAuthenticationAction.ShowToast -> {
                        platformActions.showMessage(action.message)
                    }
                }
            }.launchIn(scope)
        }

        DefaultAuthenticationScreen(
            modifier = Modifier,
            onEvent = viewModel::handleEvent,
            loading = state.value
        )
    }

    composable(route = Screen.LoginScreen.route) {
        val viewModel = koinViewModel<LoginViewModel>()
        val state = viewModel.loginUiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    LoginUiAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    LoginUiAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route)
                    }

                    is LoginUiAction.OnShowToast -> {
                        platformActions.showMessage(action.message)
                    }

                    LoginUiAction.OnGoToHome -> {
                        navigateAfterAuth(navController, Screen.LoginScreen.route, hasCompletedPostAuthOnboarding)
                    }
                }
            }.launchIn(scope)
        }

        LoginScreen(state = state.value, onEvent = viewModel::handleEvent)
    }

    composable(route = Screen.SignUpScreen.route) {
        val viewModel = koinViewModel<SignUpViewModel>()
        val state = viewModel.signUpUiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    SignUpUiAction.OnGoBack -> {
                        navController.popBackStack()
                    }

                    SignUpUiAction.OnGoToHome -> {
                        navigateAfterAuth(navController, Screen.SignUpScreen.route, hasCompletedPostAuthOnboarding)
                    }

                    SignUpUiAction.OnGoToLogin -> {
                        navController.navigate(Screen.LoginScreen.route)
                    }

                    is SignUpUiAction.OnShowToast -> {
                        platformActions.showMessage(action.message)
                    }
                }
            }.launchIn(scope)
        }
        SignUpScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent
        )
    }
}

private fun navigateAfterAuth(
    navController: androidx.navigation.NavHostController,
    popUpToRoute: String,
    hasCompletedPostAuthOnboarding: Boolean
) {
    if (!hasCompletedPostAuthOnboarding) {
        navController.navigate(Screen.PostAuthOnboardingScreen.route) {
            popUpTo(popUpToRoute) { inclusive = true }
        }
    } else {
        navigateToHome(navController, popUpToRoute)
    }
}
