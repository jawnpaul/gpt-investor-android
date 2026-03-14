package com.thejawnpaul.gptinvestor

import androidx.compose.ui.window.ComposeUIViewController
import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelProvider
import com.thejawnpaul.gptinvestor.analytics.mixpanel.mixpanelProviderModule
import com.thejawnpaul.gptinvestor.core.di.GPTKoinApp
import org.koin.compose.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration
import platform.UIKit.UIViewController

/**
 * Entry point called by the Swift iosApp.
 *
 * [mixpanelProvider] is the Swift-side [MixpanelProvider] implementation
 * (SwiftMixpanelProvider). It is injected directly into the Koin graph here so
 * that [mixpanelProviderModule] overrides the default [NoOpMixpanelProvider] that
 * @ComponentScan would otherwise register.
 *
 * Passing the provider as a constructor argument (rather than calling
 * loadKoinModules() from Swift) avoids the timing problem that arises because
 * KoinApplication is compose-scoped — the Koin context does not exist until the
 * first composition, so a Swift-side loadKoinModules() call would race with it.
 */
fun mainViewController(mixpanelProvider: MixpanelProvider): UIViewController = ComposeUIViewController {
    KoinApplication(
        configuration = koinConfiguration<GPTKoinApp> {
            printLogger()
            allowOverride(true)
            modules(mixpanelProviderModule(mixpanelProvider))
        }
    ) {
        App()
    }
}
