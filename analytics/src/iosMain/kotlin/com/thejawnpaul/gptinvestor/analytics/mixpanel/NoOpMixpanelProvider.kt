package com.thejawnpaul.gptinvestor.analytics.mixpanel

import org.koin.core.annotation.Singleton

/**
 * Default no-op MixpanelProvider for iOS. Auto-discovered by @ComponentScan and registered
 * as the MixpanelProvider binding until the iosApp overrides it with SwiftMixpanelProvider.
 */
@Singleton(binds = [MixpanelProvider::class])
class NoOpMixpanelProvider : MixpanelProvider {
    override fun logEvent(eventName: String, params: Map<String, Any>) = Unit
    override fun logViewEvent(screenName: String) = Unit
    override fun identifyUser(userId: String, params: Map<String, Any>) = Unit
    override fun resetUser() = Unit
}
