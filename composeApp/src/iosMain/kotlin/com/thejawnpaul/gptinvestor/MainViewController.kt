package com.thejawnpaul.gptinvestor

import androidx.compose.ui.window.ComposeUIViewController
import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelProvider
import com.thejawnpaul.gptinvestor.analytics.mixpanel.mixpanelProviderModule
import com.thejawnpaul.gptinvestor.core.di.GPTKoinApp
import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import com.thejawnpaul.gptinvestor.core.platform.YoutubePlayerProvider
import com.thejawnpaul.gptinvestor.core.platform.youtubePlayerProviderModule
import com.thejawnpaul.gptinvestor.features.authentication.domain.googleSignInProviderModule
import org.koin.compose.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration
import platform.UIKit.UIViewController

/**
 * Entry point called by the Swift iosApp.
 *
 * [mixpanelProvider], [youtubePlayerProvider], and [googleSignInProvider] are Swift-side
 * implementations of their respective KMP interfaces, injected into the Koin graph here
 * so they override the default no-op bindings registered by @ComponentScan.
 *
 * Passing providers as constructor arguments (rather than calling loadKoinModules()
 * from Swift) avoids the race condition where the Koin context does not exist until
 * the first composition.
 */
fun mainViewController(
    mixpanelProvider: MixpanelProvider,
    youtubePlayerProvider: YoutubePlayerProvider,
    googleSignInProvider: GoogleSignInProvider
): UIViewController = ComposeUIViewController {
    KoinApplication(
        configuration = koinConfiguration<GPTKoinApp> {
            printLogger()
            allowOverride(true)
            modules(
                mixpanelProviderModule(mixpanelProvider),
                youtubePlayerProviderModule(youtubePlayerProvider),
                googleSignInProviderModule(googleSignInProvider)
            )
        }
    ) {
        App()
    }
}
