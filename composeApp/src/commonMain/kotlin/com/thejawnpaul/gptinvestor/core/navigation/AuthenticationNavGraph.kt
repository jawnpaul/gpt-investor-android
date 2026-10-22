package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationViewModel
import com.thejawnpaul.gptinvestor.features.authentication.presentation.FORGOT_PASSWORD_EMAIL_ARG
import com.thejawnpaul.gptinvestor.features.authentication.presentation.ForgotPasswordScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.ForgotPasswordUiAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.ForgotPasswordViewModel
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginUiAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.LoginViewModel
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpScreen
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpUiAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.SignUpViewModel
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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
                        navController.navigateClearingBackStack(GuestScreen.GuestHomeTab.route)
                    }

                    DefaultAuthenticationAction.OnGoToSignUp -> {
                        navController.navigate(Screen.SignUpScreen.route)
                    }

                    is DefaultAuthenticationAction.ShowToast -> {
                        platformActions.showMessage(action.message)
                    }

                    is DefaultAuthenticationAction.OpenUrl -> {
                        platformActions.openUrl(action.url)
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
                        navigateAfterAuth(navController, hasCompletedPostAuthOnboarding)
                    }

                    is LoginUiAction.OnGoToForgotPassword -> {
                        navController.navigate(Screen.ForgotPasswordScreen.createRoute(action.email))
                    }

                    is LoginUiAction.InvalidSubmit -> Unit
                }
            }.launchIn(scope)
        }
        val invalidSubmits = remember(viewModel) {
            viewModel.actions.filterIsInstance<LoginUiAction.InvalidSubmit>().map { it.firstInvalid }
        }
        LoginScreen(state = state.value, onEvent = viewModel::handleEvent, invalidSubmits = invalidSubmits)
    }

    composable(
        route = Screen.ForgotPasswordScreen.route,
        arguments = listOf(
            navArgument(FORGOT_PASSWORD_EMAIL_ARG) {
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) {
        val viewModel = koinViewModel<ForgotPasswordViewModel>()
        val state = viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            viewModel.actions.onEach { action ->
                when (action) {
                    ForgotPasswordUiAction.OnGoBack -> {
                        navController.navigateUp()
                    }

                    is ForgotPasswordUiAction.OnShowToast -> {
                        platformActions.showMessage(action.message)
                    }
                }
            }.launchIn(scope)
        }

        ForgotPasswordScreen(state = state.value, onEvent = viewModel::handleEvent)
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
                        navigateAfterAuth(navController, hasCompletedPostAuthOnboarding)
                    }

                    SignUpUiAction.OnGoToLogin -> {
                        navController.navigate(Screen.LoginScreen.route)
                    }

                    is SignUpUiAction.OnShowToast -> {
                        platformActions.showMessage(action.message)
                    }

                    is SignUpUiAction.InvalidSubmit -> Unit
                }
            }.launchIn(scope)
        }
        val invalidSubmits = remember(viewModel) {
            viewModel.actions.filterIsInstance<SignUpUiAction.InvalidSubmit>().map { it.firstInvalid }
        }
        SignUpScreen(
            modifier = Modifier,
            state = state.value,
            onEvent = viewModel::handleEvent,
            invalidSubmits = invalidSubmits
        )
    }
}

private fun navigateAfterAuth(navController: NavHostController, hasCompletedPostAuthOnboarding: Boolean) {
    val route = if (hasCompletedPostAuthOnboarding) {
        Screen.HomeTabScreen.route
    } else {
        Screen.PostAuthOnboardingScreen.route
    }
    navController.navigateClearingBackStack(route)
}
