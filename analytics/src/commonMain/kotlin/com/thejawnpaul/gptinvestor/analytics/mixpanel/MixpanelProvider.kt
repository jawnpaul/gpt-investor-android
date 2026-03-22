package com.thejawnpaul.gptinvestor.analytics.mixpanel

/**
 * Bridge interface between the KMP analytics module and a platform-specific Mixpanel
 * implementation. Compiled to an ObjC protocol (MixpanelProvider) visible to Swift.
 *
 * On Android this interface is never used — MixpanelLogger.android.kt calls the Android SDK directly.
 * On iOS, the iosApp provides a Swift class conforming to this protocol and registers it in Koin.
 *
 * initialize() is intentionally excluded. The Swift implementation handles SDK init in its
 * own init(), reading the token from the exported BuildConfig or a Swift-side constant.
 */
interface MixpanelProvider {
    fun logEvent(eventName: String, params: Map<String, Any>)
    fun logViewEvent(screenName: String)
    fun identifyUser(userId: String, params: Map<String, Any>)
    fun resetUser()
}
