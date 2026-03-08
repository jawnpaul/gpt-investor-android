package com.thejawnpaul.gptinvestor.analytics.mixpanel

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Returns a Koin module that overrides the default NoOpMixpanelProvider with a real provider.
 * The iosApp calls this with a Swift SwiftMixpanelProvider instance before or immediately
 * after startKoin, before any analytics event is fired.
 *
 * The iosApp's startKoin block must set allowOverride = true for the override to take effect.
 *
 * Usage from Swift:
 *   startKoin { allowOverride(true) ... }
 *   loadKoinModules(MixpanelProviderModuleKt.mixpanelProviderModule(provider: SwiftMixpanelProvider()))
 */
fun mixpanelProviderModule(provider: MixpanelProvider): Module = module {
    single<MixpanelProvider> { provider }
}
