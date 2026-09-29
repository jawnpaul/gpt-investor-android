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
import com.thejawnpaul.gptinvestor.core.session.GuestRateLimitNotifier
import com.thejawnpaul.gptinvestor.core.session.GuestSessionEvent
import com.thejawnpaul.gptinvestor.core.session.GuestSessionNotifier
import com.thejawnpaul.gptinvestor.core.session.GuestSessionReason
import com.thejawnpaul.gptinvestor.core.session.GuestSessionSource
import com.thejawnpaul.gptinvestor.core.session.JwtExpiryChecker
import com.thejawnpaul.gptinvestor.features.authentication.presentation.GuestSessionExpiredSheet
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.GuestRateLimitBottomSheet
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.features.splash.AnimatedSplashScreen
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import kotlin.time.Clock
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject

@Composable
fun App(modifier: Modifier = Modifier, deepLinkRoute: String? = null, onDeepLinkConsume: () -> Unit = {}) {
    val preferences: AppPreferences = koinInject()
    val tokenSyncManager: TokenSyncManager = koinInject()
    val guestRateLimitNotifier: GuestRateLimitNotifier = koinInject()
    val guestSessionNotifier: GuestSessionNotifier = koinInject()
    val tokenStorage: TokenStorage = koinInject()

    val themePreference by preferences.themePreference.collectAsState(initial = "System")
    val isUserSignedIn by preferences.isUserLoggedIn.collectAsState(initial = false)
    val isGuestSignedIn by preferences.isGuestLoggedIn.collectAsState(initial = false)
    val hasCompletedOnboarding by preferences.hasCompletedOnboarding.collectAsState(initial = null)
    val hasCompletedPostAuthOnboarding by preferences.hasCompletedPostAuthOnboarding.collectAsState(initial = null)

    var showSplash by remember { mutableStateOf(true) }
    var isNavGraphReady by remember { mutableStateOf(false) }
    var showGuestRateLimitSheet by remember { mutableStateOf(false) }
    var showGuestSessionSheet by remember { mutableStateOf(false) }
    var guestSessionReason by remember { mutableStateOf(GuestSessionReason.Expired) }
    var pendingReturnTicker by remember { mutableStateOf<String?>(null) }

    val navController = rememberNavController()

    val currentOnDeepLinkConsume by rememberUpdatedState(onDeepLinkConsume)

    LaunchedEffect(Unit) {
        tokenSyncManager.syncToken()
    }

    LaunchedEffect(Unit) {
        guestRateLimitNotifier.signal.collect {
            if (!showGuestRateLimitSheet && isGuestSignedIn == true) showGuestRateLimitSheet = true
        }
    }

    LaunchedEffect(Unit) {
        guestSessionNotifier.signal.collect { event ->
            if (!showGuestSessionSheet && isGuestSignedIn == true) {
                guestSessionReason = event.reason
                pendingReturnTicker = event.ticker
                showGuestSessionSheet = true
            }
        }
    }

    // Check guest token expiry at app start, once after splash
    LaunchedEffect(showSplash) {
        if (!showSplash) {
            val isGuest = preferences.isGuestLoggedIn.first() == true
            if (isGuest) {
                val token = tokenStorage.getAccessToken()
                if (token != null) {
                    val nowSeconds = Clock.System.now().epochSeconds
                    if (JwtExpiryChecker.isExpired(token, nowSeconds)) {
                        guestSessionNotifier.notify(
                            GuestSessionEvent(
                                reason = GuestSessionReason.Expired,
                                source = GuestSessionSource.Launch
                            )
                        )
                    }
                }
            }
        }
    }

    // After sign-in, return to context (company screen) if we have a pending ticker
    LaunchedEffect(isUserSignedIn, hasCompletedPostAuthOnboarding, isNavGraphReady) {
        if (isNavGraphReady && isUserSignedIn == true && hasCompletedPostAuthOnboarding == true) {
            val ticker = pendingReturnTicker
            if (ticker != null) {
                pendingReturnTicker = null
                navController.navigate(Screen.CompanyDetailScreen.createRoute(ticker))
            }
        }
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
                isGuestSignedIn = isGuestSignedIn == true,
                hasCompletedOnboarding = hasCompletedOnboarding ?: false,
                hasCompletedPostAuthOnboarding = hasCompletedPostAuthOnboarding ?: false
            )

            if (showGuestRateLimitSheet) {
                GuestRateLimitBottomSheet(
                    onDismiss = { showGuestRateLimitSheet = false },
                    onSignIn = {
                        showGuestRateLimitSheet = false
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }

            if (showGuestSessionSheet) {
                GuestSessionExpiredSheet(
                    reason = guestSessionReason,
                    onDismiss = { showGuestSessionSheet = false },
                    onSignUp = {
                        showGuestSessionSheet = false
                        navController.navigate(Screen.SignUpScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    onLogin = {
                        showGuestSessionSheet = false
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }

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
