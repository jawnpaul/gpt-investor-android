package com.thejawnpaul.gptinvestor.core.platform

import platform.UIKit.UIView

/**
 * ObjC-visible protocol bridging YouTube playback to Swift.
 *
 * Kotlin/Native exposes this interface as an ObjC protocol in the ComposeApp framework,
 * making it implementable in Swift using YouTubeiOSPlayerHelper (SPM) — which cannot be
 * used from Kotlin/Native directly because YTPlayerView is ObjC but its delegate pattern
 * is easier to wire up entirely on the Swift side.
 *
 * The default binding is [NoOpYoutubePlayerProvider]. The iosApp overrides it at startup
 * via [youtubePlayerProviderModule], mirroring the MixpanelProvider bridge pattern.
 */
interface YoutubePlayerProvider {
    /**
     * Creates and returns a UIView that plays the given YouTube video.
     * The caller embeds the view via UIKitView in Compose.
     */
    fun createPlayerView(videoId: String, autoplay: Boolean, showControls: Boolean): UIView
}
