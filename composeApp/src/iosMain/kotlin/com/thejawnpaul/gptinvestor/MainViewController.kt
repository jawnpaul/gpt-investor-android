package com.thejawnpaul.gptinvestor

import androidx.compose.ui.window.ComposeUIViewController
import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelProvider
import com.thejawnpaul.gptinvestor.core.navigation.NativeTabSwitcherRegistry
import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import com.thejawnpaul.gptinvestor.core.platform.YoutubePlayerProvider
import platform.UIKit.UIViewController

/**
 * Entry point called by the Swift iosApp for iOS < 26.
 *
 * Koin is started globally by [initKoin] before this is called, so provider
 * params are kept only to preserve the Swift call-site signature — they are
 * not used here.
 */
fun mainViewController(
    mixpanelProvider: MixpanelProvider,
    youtubePlayerProvider: YoutubePlayerProvider,
    googleSignInProvider: GoogleSignInProvider
): UIViewController = ComposeUIViewController {
    App()
}

/**
 * Per-tab entry point for the iOS 26 native TabView / liquid glass architecture.
 *
 * [startRoute] is the nav-graph route that this tab's NavHost should begin at
 * (e.g. "home_tab_screen"). Koin must already be running via [initKoin] before
 * this is called.
 */
fun tabViewController(startRoute: String): UIViewController =
    ComposeUIViewController(configure = { onFocusBehavior = OnFocusBehavior.DoNothing }) {
        App(
            useNativeNavigation = true,
            startRoute = startRoute,
            onSwitchNativeTab = { NativeTabSwitcherRegistry.switchTo(it) }
        )
    }
