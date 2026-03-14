package com.thejawnpaul.gptinvestor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.core.navigation.SetUpNavGraph
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.presentation.AuthenticationAction
import com.thejawnpaul.gptinvestor.features.authentication.presentation.AuthenticationViewModel
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationScreen
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.OnboardingScreen
import com.thejawnpaul.gptinvestor.features.splash.AnimatedSplashScreen
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(modifier: Modifier = Modifier, deepLinkRoute: String? = null, onDeepLinkConsume: () -> Unit = {}) {
    val preferences: AppPreferences = koinInject()
    val authenticationViewModel: AuthenticationViewModel = koinViewModel()
    val platformActions: PlatformActions = koinInject()

    val themePreference by preferences.themePreference.collectAsState(initial = "Dark")
    val isUserSignedIn by preferences.isUserLoggedIn.collectAsState(initial = false)
    val isFirstInstall by preferences.isFirstInstall.collectAsState(initial = true)

    var showSplash by remember { mutableStateOf(true) }
    var isNavGraphReady by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val currentOnDeepLinkConsume by rememberUpdatedState(onDeepLinkConsume)

    LaunchedEffect(Unit) {
        authenticationViewModel.actions.onEach { action ->
            when (action) {
                is AuthenticationAction.OnLogin -> platformActions.showMessage(action.message)
                is AuthenticationAction.OnSignUp -> platformActions.showMessage(action.message)
            }
        }.launchIn(scope)
    }

    LaunchedEffect(deepLinkRoute, isNavGraphReady, isUserSignedIn, isFirstInstall) {
        if (deepLinkRoute != null &&
            isNavGraphReady &&
            isUserSignedIn == true &&
            isFirstInstall == false
        ) {
            try {
                val navigationRoute = getNavigationRouteFromDeepLink(deepLinkRoute)
                navController.navigate(route = navigationRoute) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
            } finally {
                currentOnDeepLinkConsume()
            }
        }
    }

    GPTInvestorTheme(userThemePreference = themePreference) {
        if (showSplash) {
            AnimatedSplashScreen(
                modifier = modifier,
                onSplashFinish = {
                    showSplash = false
                }
            )
        } else {
            if (isUserSignedIn == true && isFirstInstall == false) {
                Surface(
                    modifier = modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetUpNavGraph(navController = navController)

                    LaunchedEffect(Unit) {
                        isNavGraphReady = true
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    isNavGraphReady = false
                }

                if (isUserSignedIn == false || isUserSignedIn == null) {
                    DefaultAuthenticationScreen(
                        modifier = Modifier,
                        authViewModel = authenticationViewModel
                    )
                } else {
                    OnboardingScreen(modifier = Modifier, onFinishOnboarding = {
                        scope.launch {
                            preferences.setIsFirstInstall(false)
                        }
                    })
                }
            }
        }
    }
}

private fun getNavigationRouteFromDeepLink(deepLink: String): String = when {
    deepLink.contains("tidbit_detail_screen") -> {
        val tidbitId = extractTidbitIdFromDeepLink(deepLink)
        tidbitId?.let {
            Screen.TidbitDetailScreen.createRoute(tidbitId = it)
        } ?: Screen.TidbitScreen.route
    }

    deepLink.contains("discover") -> {
        Screen.DiscoverTabScreen.route
    }

    else -> {
        Screen.HomeScreen.route
    }
}

private fun extractTidbitIdFromDeepLink(deepLink: String): String? = try {
    val parts = deepLink.split("/")
    if (parts.size >= 4 && parts[3] == "tidbit_detail_screen") {
        parts[4]
    } else {
        null
    }
} catch (_: Exception) {
    null
}
