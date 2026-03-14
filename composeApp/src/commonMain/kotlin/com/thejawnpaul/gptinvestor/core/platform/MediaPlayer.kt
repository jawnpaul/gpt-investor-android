package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface MediaPlayer {
    @Composable
    fun Video(
        url: String,
        autoplay: Boolean,
        loop: Boolean,
        isYoutube: Boolean,
        showControls: Boolean,
        youtubeVideoId: String?,
        modifier: Modifier
    )

    @Composable
    fun Audio(audioUrl: String, modifier: Modifier)
}
