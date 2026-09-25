@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import com.thejawnpaul.gptinvestor.features.premium.presentation.ui.PremiumSheet
import com.thejawnpaul.gptinvestor.features.premium.presentation.viewmodel.PremiumViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

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
    val premiumViewModel = koinViewModel<PremiumViewModel>()
    val premiumState by premiumViewModel.state.collectAsState()

    val startDestination = initialDestination(isUserSignedIn, isGuestSignedIn, hasCompletedOnboarding)

    if (premiumState.isVisible) {
        PremiumSheet(
            state = premiumState,
            onDismiss = premiumViewModel::dismiss,
            onPurchase = premiumViewModel::purchase
        )
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(innerPadding).then(modifier)
                ) {
                    onboardingNavGraph(navController)
                    authenticationNavGraph(navController, platformActions)
                    investorNavGraph(navController, platformActions)
                    discoverNavGraph(navController)
                    browseStocksNavGraph(navController)
                    companyNavGraph(navController, platformActions)
                    conversationNavGraph(navController, platformActions)
                    historyNavGraph(navController, platformActions)
                    topPickNavGraph(navController, platformActions)
                    tidbitNavGraph(navController, platformActions)
                    settingsNavGraph(navController)
                    searchNavGraph(navController)
                    trendingNavGraph(navController)
                    watchlistNavGraph(navController, platformActions)
                    profileNavGraph(navController, platformActions)
                    guestNavGraph(navController, platformActions, platformContext)
                }
            }
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
