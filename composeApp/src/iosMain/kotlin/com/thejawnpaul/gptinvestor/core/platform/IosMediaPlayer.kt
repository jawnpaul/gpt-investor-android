package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.core.annotation.Singleton

@Singleton(binds = [MediaPlayer::class])
class IosMediaPlayer : MediaPlayer {
    @Composable
    override fun Video(
        url: String,
        autoplay: Boolean,
        loop: Boolean,
        isYoutube: Boolean,
        showControls: Boolean,
        youtubeVideoId: String?,
        modifier: Modifier
    ) {
        // Simple stub for iOS v1
    }

    @Composable
    override fun Audio(audioUrl: String, modifier: Modifier) {
        // Simple stub for iOS v1
    }
}
