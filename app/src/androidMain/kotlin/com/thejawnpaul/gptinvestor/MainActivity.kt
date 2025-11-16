package com.thejawnpaul.gptinvestor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.thejawnpaul.gptinvestor.core.utility.setActivityProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setActivityProvider { this }
        setContent {
            var deepLinkRoute by remember { mutableStateOf<String?>(null) }
            // Handle deep link from intent
            LaunchedEffect(Unit) {
                handleDeepLinkFromIntent(intent) { route ->
                    deepLinkRoute = route
                }
            }
            App(deepLinkRoute)
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
}
