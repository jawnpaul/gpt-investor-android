@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.platform.ToastMessenger
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import com.thejawnpaul.gptinvestor.features.premium.presentation.ui.PremiumSheet
import com.thejawnpaul.gptinvestor.features.premium.presentation.viewmodel.PremiumViewModel
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastResult
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isUserSignedIn: Boolean = false,
    isGuestSignedIn: Boolean = false,
    hasCompletedOnboarding: Boolean = false,
    hasCompletedPostAuthOnboarding: Boolean = false
) {
    val platformContext: PlatformContext = koinInject()
    val platformActions: PlatformActions = koinInject()
    val toastMessenger: ToastMessenger = koinInject()
    val toastState = rememberGiToastState()
    val premiumViewModel = koinViewModel<PremiumViewModel>()
    val premiumState by premiumViewModel.state.collectAsState()

    val startDestination = remember {
        initialDestination(
            isUserSignedIn,
            isGuestSignedIn,
            hasCompletedOnboarding,
            hasCompletedPostAuthOnboarding
        )
    }

    LaunchedEffect(toastMessenger, toastState) {
        toastMessenger.messages.collect { toast ->
            launch {
                val result = toastState.show(message = toast.message, icon = toast.icon, actionText = toast.actionText)
                if (result == GiToastResult.ActionPerformed) toast.onAction()
            }
        }
    }

    if (premiumState.isVisible) {
        PremiumSheet(
            state = premiumState,
            onDismiss = premiumViewModel::dismiss,
            onPurchase = premiumViewModel::purchase
        )
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        enterTransition = navEnterTransition,
                        exitTransition = navExitTransition,
                        popEnterTransition = navPopEnterTransition,
                        popExitTransition = navPopExitTransition,
                        modifier = Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding)
                    ) {
                        onboardingNavGraph(navController)
                        postAuthOnboardingNavGraph(navController)
                        authenticationNavGraph(navController, platformActions, hasCompletedPostAuthOnboarding)
                        investorNavGraph(navController)
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

            GiToastHost(
                state = toastState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                    .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x4)
            )
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
                } else if (!hasCompletedPostAuthOnboarding) {
                    navController.navigate(Screen.PostAuthOnboardingScreen.route) {
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
    hasCompletedOnboarding: Boolean,
    hasCompletedPostAuthOnboarding: Boolean
): String = when {
    isUserSignedIn && !hasCompletedPostAuthOnboarding -> Screen.PostAuthOnboardingScreen.route
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

internal fun NavHostController.navigateClearingBackStack(route: String) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
    }
}
