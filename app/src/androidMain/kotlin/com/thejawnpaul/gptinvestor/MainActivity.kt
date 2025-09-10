package com.thejawnpaul.gptinvestor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.thejawnpaul.gptinvestor.core.navigation.SetUpNavGraph
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.core.utility.ActivityContext
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DefaultAuthenticationScreen
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.OnboardingScreen
import com.thejawnpaul.gptinvestor.features.splash.AnimatedSplashScreen
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val preferences: GPTInvestorPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        ActivityContext.set(this)
        super.onCreate(savedInstanceState)
        setContent {
            val themePreference by preferences.themePreference.collectAsState(initial = "Dark")
            val isUserSignedIn by preferences.isUserLoggedIn.collectAsState(initial = false)
            val isFirstInstall by preferences.isFirstInstall.collectAsState(initial = true)

            var showSplash by remember { mutableStateOf(true) }

            val scope = rememberCoroutineScope()

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
                            val navController = rememberNavController()
                            SetUpNavGraph(navController = navController)
                        }
                    } else {
                        // if user is not signed in show auth otherwise show onboarding
                        if (isUserSignedIn == false || isUserSignedIn == null) {
                            // LOGIN SCREEN
                            DefaultAuthenticationScreen(
                                modifier = Modifier,
                                onAuthSuccess = {
                                    Toast.makeText(
                                        this,
                                        "Authentication successful",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    // if user first time login, navigate to onboarding screen else navigate to home screen
                                },
                                onAuthFailure = {
                                    Toast.makeText(
                                        this,
                                        "Authentication failed",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
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
}
