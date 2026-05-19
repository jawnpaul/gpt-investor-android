@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import org.koin.compose.koinInject

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isUserSignedIn: Boolean = false,
    isGuestSignedIn: Boolean = false,
    hasCompletedOnboarding: Boolean = false
) {
    val platformContext: PlatformContext = koinInject()
    val platformActions: PlatformActions = koinInject()

    val startDestination = initialDestination(isUserSignedIn, isGuestSignedIn, hasCompletedOnboarding)

    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier
            ) {
                onboardingNavGraph(navController)
                authenticationNavGraph(navController, platformActions, isGuestSignedIn)
                investorNavGraph(navController, platformActions)
                discoverNavGraph(navController)
                companyNavGraph(navController, platformActions)
                conversationNavGraph(navController, platformActions, platformContext, isGuestSignedIn)
                historyNavGraph(navController, platformActions, platformContext, isGuestSignedIn)
                topPickNavGraph(navController, platformActions)
                tidbitNavGraph(navController, platformActions)
                settingsNavGraph(navController)
                searchNavGraph(navController)
                trendingNavGraph(navController)
            }
        }
    }

    LaunchedEffect(isUserSignedIn, isGuestSignedIn) {
        val currentRoute = navController.currentDestination?.route ?: return@LaunchedEffect
        if (isUserSignedIn || isGuestSignedIn) {
            if (currentRoute == Screen.DefaultAuthenticationScreen.route ||
                currentRoute == Screen.OnboardingScreen.route
            ) {
                navigateToHome(navController, currentRoute!!)
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
    isUserSignedIn || isGuestSignedIn -> Screen.HomeTabScreen.route
    !hasCompletedOnboarding -> Screen.OnboardingScreen.route
    else -> Screen.DefaultAuthenticationScreen.route
}

internal fun navigateToHome(navController: NavHostController, popUpToRoute: String) {
    navController.navigate(Screen.HomeTabScreen.route) {
        popUpTo(popUpToRoute) { inclusive = true }
    }
}
