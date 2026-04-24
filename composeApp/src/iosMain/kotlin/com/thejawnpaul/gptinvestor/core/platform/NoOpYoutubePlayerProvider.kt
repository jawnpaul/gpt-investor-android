package com.thejawnpaul.gptinvestor.core.platform

import org.koin.core.annotation.Singleton
import platform.UIKit.UIView

/**
 * Fallback no-op YoutubePlayerProvider. Not registered via @Singleton — the real
 * SwiftYoutubePlayerProvider is always injected by mainViewController before Koin starts,
 * so @ComponentScan must not discover this class or it will override the real provider.
 */
@Singleton(binds = [YoutubePlayerProvider::class])
class NoOpYoutubePlayerProvider : YoutubePlayerProvider {
    override fun createPlayerView(videoId: String, autoplay: Boolean, showControls: Boolean): UIView = UIView()
}
