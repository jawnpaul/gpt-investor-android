package com.thejawnpaul.gptinvestor.core.platform

import platform.UIKit.UIView

/**
 * Fallback no-op YoutubePlayerProvider. Not registered via @Singleton — the real
 * SwiftYoutubePlayerProvider is always injected by mainViewController before Koin starts,
 * so @ComponentScan must not discover this class or it will override the real provider.
 */
class NoOpYoutubePlayerProvider : YoutubePlayerProvider {
    override fun createPlayerView(videoId: String, autoplay: Boolean, showControls: Boolean): UIView = UIView()
}
