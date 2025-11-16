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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import com.thejawnpaul.gptinvestor.core.navigation.DiscoverTabScreen
import com.thejawnpaul.gptinvestor.core.navigation.HomeScreen
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.core.navigation.SetUpNavGraph
import com.thejawnpaul.gptinvestor.core.navigation.TidbitDetailScreen
import com.thejawnpaul.gptinvestor.core.navigation.TidbitScreen
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.core.utility.ToastDuration
import com.thejawnpaul.gptinvestor.core.utility.ToastManager
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationScreen
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.OnboardingScreen
import com.thejawnpaul.gptinvestor.features.splash.AnimatedSplashScreen
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App(deepLinkRoute: String?) {
    val preferences = koinInject<GPTInvestorPreferences>()
    val themePreference by preferences.themePreference.collectAsState(initial = "Dark")
    val isUserSignedIn by preferences.isUserLoggedIn.collectAsState(initial = false)
    val isFirstInstall by preferences.isFirstInstall.collectAsState(initial = true)

    var showSplash by remember { mutableStateOf(true) }
    var currentDeepLinkRoute by remember { mutableStateOf(deepLinkRoute) }
    var isNavGraphReady by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    setSingletonImageLoaderFactory {
        ImageLoader.Builder(it)
            .crossfade(true)
            .build()
    }

    // Navigate when deep link is available AND nav graph is ready
    LaunchedEffect(deepLinkRoute, isNavGraphReady) {
        if (currentDeepLinkRoute != null &&
            isNavGraphReady &&
            isUserSignedIn == true &&
            isFirstInstall == false
        ) {
            try {
                val navigationRoute = getNavigationRouteFromDeepLink(currentDeepLinkRoute!!)
                navController.navigate(route = navigationRoute) {
                    // Optional: Clear back stack
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
                currentDeepLinkRoute = null
            } catch (e: Exception) {
                println("Navigation error: ${e.message}")
                currentDeepLinkRoute = null
            }
        }
    }

    GPTInvestorTheme(
        userThemePreference = themePreference
    ) {
        if (showSplash) {
            AnimatedSplashScreen {
                showSplash = false
            }
        } else {
            if (isUserSignedIn == true && isFirstInstall == false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetUpNavGraph(navController = navController)

                    // Mark nav graph as ready after composition
                    LaunchedEffect(Unit) {
                        isNavGraphReady = true
                    }
                }
            } else {
                // Reset nav graph ready state when not showing main nav
                LaunchedEffect(Unit) {
                    isNavGraphReady = false
                }

                // if user is not signed in show auth otherwise show onboarding
                if (isUserSignedIn == false || isUserSignedIn == null) {
                    val toastManager by remember { mutableStateOf(ToastManager()) }
                    // LOGIN SCREEN
                    DefaultAuthenticationScreen(
                        modifier = Modifier,
                        onAuthSuccess = {
                            toastManager.showToast(
                                "Authentication successful",
                                ToastDuration.Short
                            )
                            // if user first time login, navigate to onboarding screen else navigate to home screen
                        },
                        onAuthFailure = {
                            toastManager.showToast(
                                "Authentication failed",
                                ToastDuration.Short
                            )
                        }
                    )
                } else {
                    // ONBOARDING SCREEN
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

private fun getNavigationRouteFromDeepLink(deepLink: String): Screen = when {
    deepLink.contains("tidbit_detail_screen") -> {
        val tidbitId = extractTidbitIdFromDeepLink(deepLink)
        tidbitId?.let {
            TidbitDetailScreen(tidbitId = it)
        } ?: TidbitScreen
    }
    deepLink.contains("discover") -> {
        DiscoverTabScreen
    }
    else -> {
        HomeScreen
    }
}

private fun extractTidbitIdFromDeepLink(deepLink: String): String? {
    // Extract tidbit ID from "app://gpt-investor/tidbit_detail_screen/abcde"
    return try {
        val parts = deepLink.split("/")
        if (parts.size >= 4 && parts[3] == "tidbit_detail_screen") {
            parts[4] // Returns "abcde"
        } else {
            null
        }
    } catch (e: Exception) {
        println("Error extracting tidbit ID: ${e.message}")
        null
    }
}