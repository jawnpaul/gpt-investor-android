package com.thejawnpaul.gptinvestor.analytics.mixpanel

/**
 * Fallback no-op MixpanelProvider. Not registered via @Singleton — the real
 * SwiftMixpanelProvider is always injected by mainViewController before Koin starts,
 * so @ComponentScan must not discover this class, or it will override the real provider.
 */
class NoOpMixpanelProvider : MixpanelProvider {
    override fun logEvent(eventName: String, params: Map<String, Any>) = Unit
    override fun logViewEvent(screenName: String) = Unit
    override fun identifyUser(userId: String, params: Map<String, Any>) = Unit
    override fun resetUser() = Unit
}
