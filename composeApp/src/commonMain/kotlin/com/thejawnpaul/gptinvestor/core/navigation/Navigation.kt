@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import org.koin.compose.koinInject

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isUserSignedIn: Boolean = false,
    isGuestSignedIn: Boolean = false,
    hasCompletedOnboarding: Boolean = false,
    startRoute: String? = null,
    useNativeNavigation: Boolean = false,
    onSwitchNativeTab: ((String) -> Unit)? = null
) {
    val platformContext: PlatformContext = koinInject()
    val platformActions: PlatformActions = koinInject()

    val startDestination = if (startRoute != null && (isUserSignedIn || isGuestSignedIn)) {
        startRoute
    } else {
        initialDestination(isUserSignedIn, isGuestSignedIn, hasCompletedOnboarding)
    }

    val navContent: @Composable (Modifier) -> Unit = { contentModifier ->
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this,
                LocalTabSwitcher provides onSwitchNativeTab
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = contentModifier
                ) {
                    onboardingNavGraph(navController)
                    authenticationNavGraph(navController, platformActions)
                    investorNavGraph(navController, platformActions)
                    discoverNavGraph(navController)
                    companyNavGraph(navController, platformActions)
                    conversationNavGraph(navController, platformActions, platformContext)
                    historyNavGraph(navController, platformActions, platformContext)
                    topPickNavGraph(navController, platformActions)
                    tidbitNavGraph(navController, platformActions)
                    settingsNavGraph(navController)
                    searchNavGraph(navController)
                    trendingNavGraph(navController)
                    watchlistNavGraph(navController)
                    profileNavGraph(navController)
                    guestNavGraph(navController, platformActions, platformContext)
                }
            }
        }
    }

    if (useNativeNavigation) {
        navContent(Modifier.then(modifier))
    } else {
        Scaffold(bottomBar = { BottomNavBar(navController) }) { innerPadding ->
            navContent(Modifier.padding(innerPadding).then(modifier))
        }
    }

    LaunchedEffect(isUserSignedIn, isGuestSignedIn) {
        val currentRoute = navController.currentDestination?.route ?: return@LaunchedEffect
        if (isUserSignedIn || isGuestSignedIn) {
            if (currentRoute == Screen.DefaultAuthenticationScreen.route ||
                currentRoute == Screen.OnboardingScreen.route
            ) {
                if (isGuestSignedIn) {
                    navController.navigate(GuestScreen.GuestHomeTab.route) {
                        popUpTo(currentRoute) { inclusive = true }
                    }
                } else {
                    navigateToHome(navController, currentRoute)
                }
            }
        } else {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute != Screen.OnboardingScreen.route) {
                navController.navigate(Screen.DefaultAuthenticationScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}

private fun initialDestination(
    isUserSignedIn: Boolean,
    isGuestSignedIn: Boolean,
    hasCompletedOnboarding: Boolean
): String = when {
    isUserSignedIn -> Screen.HomeTabScreen.route
    isGuestSignedIn -> GuestScreen.GuestHomeTab.route
    !hasCompletedOnboarding -> Screen.OnboardingScreen.route
    else -> Screen.DefaultAuthenticationScreen.route
}

internal fun navigateToHome(navController: NavHostController, popUpToRoute: String) {
    navController.navigate(Screen.HomeTabScreen.route) {
        popUpTo(popUpToRoute) { inclusive = true }
    }
}
