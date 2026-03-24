package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Returns a Koin module that overrides [NoOpYoutubePlayerProvider] with the real
 * Swift-side implementation. Pass a [SwiftYoutubePlayerProvider] instance from iosApp
 * and include the result in the `modules(...)` call inside `koinConfiguration<GPTKoinApp>`.
 *
 * Usage from Swift (via MainViewController):
 *   MainViewControllerKt.mainViewController(
 *       mixpanelProvider: ...,
 *       youtubePlayerProvider: SwiftYoutubePlayerProvider()
 *   )
 */
fun youtubePlayerProviderModule(provider: YoutubePlayerProvider): Module = module {
    single<YoutubePlayerProvider> { provider }
}
