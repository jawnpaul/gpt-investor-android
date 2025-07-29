package com.thejawnpaul.gptinvestor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.core.navigation.SetUpNavGraph
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationScreen
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.OnboardingScreen
import com.thejawnpaul.gptinvestor.features.splash.AnimatedSplashScreen
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferences: GPTInvestorPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val themePreference by preferences.themePreference.collectAsState(initial = "Dark")
            val isUserSignedIn by preferences.isUserLoggedIn.collectAsState(initial = false)
            val isFirstInstall by preferences.isFirstInstall.collectAsState(initial = true)

            var showSplash by remember { mutableStateOf(true) }
            var deepLinkRoute by remember { mutableStateOf<String?>(null) }
            var isNavGraphReady by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()
            val navController = rememberNavController()

            // Handle deep link from intent
            LaunchedEffect(Unit) {
                handleDeepLinkFromIntent(intent) { route ->
                    deepLinkRoute = route
                }
            }

            // Navigate when deep link is available AND nav graph is ready
            LaunchedEffect(deepLinkRoute, isNavGraphReady) {
                if (deepLinkRoute != null && isNavGraphReady &&
                    isUserSignedIn == true && isFirstInstall == false
                ) {
                    try {
                        navController.navigate(route = getScreenNameFromRoute(route = deepLinkRoute).route) {
                            // Optional: Clear back stack
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                        deepLinkRoute = null
                    } catch (e: Exception) {
                        println("Navigation error: ${e.message}")
                        deepLinkRoute = null
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
                            // LOGIN SCREEN
                            DefaultAuthenticationScreen(
                                modifier = Modifier,
                                onAuthSuccess = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Authentication successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // if user first time login, navigate to onboarding screen else navigate to home screen
                                },
                                onAuthFailure = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Authentication failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let { newIntent ->
            setIntent(newIntent)
            handleDeepLinkFromIntent(newIntent) { route ->
                // Handle deep link when app is already running
                lifecycleScope.launch {
                    // Recreating activity is not ideal but works for this case
                    recreate()
                }
            }
        }
    }

    private fun handleDeepLinkFromIntent(intent: Intent?, onDeepLink: (String) -> Unit) {
        intent?.let {
            // Check if intent came from FCM notification
            val deepLinkRoute = it.getStringExtra("deep_link")
            val notificationData = it.getStringExtra("notification_data")

            deepLinkRoute?.let { route ->
                onDeepLink(route)
            }

            // Handle other FCM data
            notificationData?.let { data ->
                // Process notification data as needed
                println("Notification data: $data")
            }
        }
    }

    private fun getScreenNameFromRoute(route: String?): Screen {
        if (route == null) {
            return Screen.HomeScreen
        }
        return if (route.contains("discover")) {
            Screen.DiscoverTabScreen
        } else {
            Screen.HomeScreen
        }
    }
}
