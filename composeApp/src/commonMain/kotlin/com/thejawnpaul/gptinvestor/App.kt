package com.thejawnpaul.gptinvestor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.core.navigation.SetUpNavGraph
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.features.splash.AnimatedSplashScreen
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import org.koin.compose.koinInject

@Composable
fun App(modifier: Modifier = Modifier, deepLinkRoute: String? = null, onDeepLinkConsume: () -> Unit = {}) {
    val preferences: AppPreferences = koinInject()
    val tokenSyncManager: TokenSyncManager = koinInject()

    val themePreference by preferences.themePreference.collectAsState(initial = "Dark")
    val isUserSignedIn by preferences.isUserLoggedIn.collectAsState(initial = false)
    val isGuestSignedIn by preferences.isGuestLoggedIn.collectAsState(initial = false)

    var showSplash by remember { mutableStateOf(true) }
    var isNavGraphReady by remember { mutableStateOf(false) }

    val navController = rememberNavController()

    val currentOnDeepLinkConsume by rememberUpdatedState(onDeepLinkConsume)

    LaunchedEffect(Unit) {
        tokenSyncManager.syncToken()
    }

    LaunchedEffect(deepLinkRoute, isNavGraphReady, isUserSignedIn, isGuestSignedIn) {
        if (deepLinkRoute != null &&
            isNavGraphReady &&
            (isUserSignedIn == true || isGuestSignedIn == true)
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

    SingletonImageLoader.setSafe { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
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
            SetUpNavGraph(
                navController = navController,
                isUserSignedIn = isUserSignedIn == true,
                isGuestSignedIn = isGuestSignedIn == true
            )

            LaunchedEffect(Unit) {
                isNavGraphReady = true
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
